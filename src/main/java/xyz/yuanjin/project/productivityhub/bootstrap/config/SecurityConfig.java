package xyz.yuanjin.project.productivityhub.bootstrap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import xyz.yuanjin.project.productivityhub.common.core.R;
import xyz.yuanjin.project.productivityhub.common.core.ResultCode;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2025/12/31 18:06</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF（无状态 JWT 不需要）并开启跨域
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                // 2. 设置无状态会话（不使用 Session）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 权限控制规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        // 增加对错误路径的放行，防止异常重定向导致的 401
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/**").permitAll() //.hasRole("ADMIN") // 监控端点限管理员
                        .anyRequest().authenticated() // 其他所有请求需要登录
                )

//                // 4. OAuth2 登录配置
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(auth -> auth.baseUri("/auth/oauth2/authorize"))
//                        .redirectionEndpoint(red -> red.baseUri("/auth/oauth2/callback/*"))
//                        // 这里需要实现一个自定义服务，用于在第三方登录成功后，自动在 user_auths 表创建/更新用户
//                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService()))
//                )

                // 5. 异常处理：自定义未登录/无权限的返回结果
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) -> {
                            res.setCharacterEncoding("UTF-8");
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            // 使用 Jackson 将 R 对象转为字符串
                            String json = new ObjectMapper().writeValueAsString(R.failed(ResultCode.UNAUTHORIZED));
                            res.getWriter().write(json);
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 手动暴露 AuthenticationManager Bean
     * 这样你才能在 UserApplicationService 中通过 @Autowired 或构造函数注入它
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
