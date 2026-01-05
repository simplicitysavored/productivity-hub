package xyz.yuanjin.project.productivityhub.bootstrap.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.yuanjin.project.productivityhub.common.util.JwtUtils;

import java.io.IOException;
import java.util.Collections;

/**
 * <p>标题： </p >
 * <p>功能：校验 Token 签名是否合法、Redis 是否过期。 </p >
 * <p>触发时机：每一个请求进入时（除了放行的路径）。 </p >
 * <p>创建日期：2026/1/4 14:57</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 从 Header 中获取 Token
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. 如果 Token 存在，尝试解析并验证
        if (StringUtils.hasText(token)) {
            String userId = JwtUtils.parseToken(token); // 验证签名和过期时间
            String jti = JwtUtils.getJti(token);        // 获取唯一 ID

            if (userId != null && jti != null) {
                // 3. 【混合模式关键点】高速校验 Redis 白名单
                // 只有在 Redis 中存在的 Token 才是真正有效的（未被撤销或踢下线）
                String redisKey = "auth:token:" + userId + ":" + jti;
                Boolean isAlive = redisTemplate.hasKey(redisKey);

                if (Boolean.TRUE.equals(isAlive)) {
                    // 4. 构建 Authentication 对象并存入 SecurityContext
                    // 这里由于是无状态，通常不需要密码（null），权限集合根据业务填入
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

                    // 注入请求详细信息（如 IP、Session ID 等）
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 正式告知 Spring Security：此用户已认证
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("用户 {} 通过 JWT 认证成功", userId);
                } else {
                    log.warn("Token 在 Redis 中已失效或已被撤销, JTI: {}", jti);
                }
            }
        }

        // 5. 放行请求，进入下一个过滤器或 Controller
        filterChain.doFilter(request, response);
    }
}
