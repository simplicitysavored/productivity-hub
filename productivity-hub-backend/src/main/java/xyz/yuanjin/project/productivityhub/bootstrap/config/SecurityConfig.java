package xyz.yuanjin.project.productivityhub.bootstrap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
@AllArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DynamicAuthorizationManager dynamicAuthManager) throws Exception {
        http
                // 禁用 CSRF（无状态 JWT 不需要）
                .csrf(AbstractHttpConfigurer::disable)
                //.cors(Customizer.withDefaults())
                // 禁用表单登录（即关闭默认登录页），Spring Security 默认会生成一个 /login 的 GET 接口来返回那个蓝色的 HTML 表单，并配置一个 UsernamePasswordAuthenticationFilter 来处理 POST 登录。禁用后，这两个都会消失。
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用 HTTP Basic 弹窗认证，禁用后，当用户未经认证访问时，浏览器不会弹出一个原生的输入用户名密码的对话框。
                .httpBasic(AbstractHttpConfigurer::disable)

                // 设置无状态会话（不使用 Session）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 权限控制规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/actuator/**").permitAll() //.hasRole("ADMIN") // 监控端点限管理员
                        // 除白名单外，所有请求走动态权限管理器
                        .anyRequest().access(dynamicAuthManager) // 其他所有请求需要登录
                )
                // 请求先到达 JwtAuthenticationFilter；如果是携带 Token 的业务请求，在这里就已经完成认证并注入 SecurityContext 了。
                // 请求随后到达 UsernamePasswordAuthenticationFilter。由于你的业务请求（如 /api/task/add）路径不是 /login，这个过滤器会检测到请求已经过认证（被你的 JWT Filter 处理了），或者路径不匹配，从而直接跳过。
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

//                // OAuth2 登录配置
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(auth -> auth.baseUri("/auth/oauth2/authorize"))
//                        .redirectionEndpoint(red -> red.baseUri("/auth/oauth2/callback/*"))
//                        // 这里需要实现一个自定义服务，用于在第三方登录成功后，自动在 user_auths 表创建/更新用户
//                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService()))
//                )

                // 异常处理：自定义未登录/无权限的返回结果
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
