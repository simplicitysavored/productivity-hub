package xyz.yuanjin.project.productivityhub.bootstrap.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import xyz.yuanjin.project.productivityhub.domain.user.entity.CustomUserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/6 15:19</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final StringRedisTemplate redisTemplate;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String AUTH_KEY = "auth:permissions";

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationManager.super.verify(authentication, object);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("动态权限管理 - 用户未登录!");
            return new AuthorizationDecision(false);
        }

        Object principal = auth.getPrincipal();

        if ("anonymousUser".equals(principal)) {
            // 如果是匿名用户，且当前路径是放行的（已经在 SecurityConfig 配置），
            // 理论上这里可以直接根据路径规则返回 true
            log.debug("匿名用户访问放行路径: {}", context.getRequest().getRequestURI());
            return new AuthorizationDecision(true);
        }

        // 安全转换：确保是 CustomUserDetails 类型后再强转
        if (!(principal instanceof CustomUserDetails user)) {
            log.warn("非预期的 Principal 类型: {}", principal.getClass().getName());
            return new AuthorizationDecision(false);
        }

        // 从 context 直接获取 request 对象
        HttpServletRequest request = context.getRequest();
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.debug("安全认证 | {} | 动态权限验证 | {}, {}", user.getUsername(), method, path);

        // 1. 获取 Redis 中的所有权限配置
        Map<Object, Object> permissions = redisTemplate.opsForHash().entries(AUTH_KEY);

        // 2. 匹配当前请求路径
        for (Map.Entry<Object, Object> entry : permissions.entrySet()) {
            String pattern = entry.getKey().toString(); // "URL:METHOD"
            String[] split = pattern.split(":");
            String urlPattern = split[0];
            String methodPattern = split[1];

            // 路径与方法同时匹配
            if (pathMatcher.match(urlPattern, path) &&
                    (methodPattern.equalsIgnoreCase("ALL") || methodPattern.equalsIgnoreCase(method))) {

                String roles = entry.getValue().toString();
                List<String> needRoles = Arrays.asList(roles.split(","));

                // 3. 校验用户持有的角色
                boolean hasRole = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(needRoles::contains);

                return new AuthorizationDecision(hasRole);
            }
        }

        // 如果数据库没配置该路径，默认认证通过即可访问，或根据安全策略返回 false
        return new AuthorizationDecision(false);
    }

    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        return AuthorizationManager.super.authorize(authentication, object);
    }
}
