package xyz.yuanjin.project.productivityhub.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.yuanjin.project.productivityhub.api.dto.UserLoginDTO;
import xyz.yuanjin.project.productivityhub.api.dto.UserRegisterDTO;
import xyz.yuanjin.project.productivityhub.application.dto.mq.LoginLogMessage;
import xyz.yuanjin.project.productivityhub.common.core.ApiException;
import xyz.yuanjin.project.productivityhub.common.util.IpUtils;
import xyz.yuanjin.project.productivityhub.common.util.JwtUtils;
import xyz.yuanjin.project.productivityhub.domain.user.entity.CustomUserDetails;
import xyz.yuanjin.project.productivityhub.domain.user.entity.UserToken;
import xyz.yuanjin.project.productivityhub.domain.user.entity.User;
import xyz.yuanjin.project.productivityhub.domain.user.entity.UserAuth;
import xyz.yuanjin.project.productivityhub.infrastructure.mq.producer.LoginLogProducer;
import xyz.yuanjin.project.productivityhub.infrastructure.repository.UserTokenMapper;
import xyz.yuanjin.project.productivityhub.infrastructure.repository.UserAuthMapper;
import xyz.yuanjin.project.productivityhub.infrastructure.repository.UserMapper;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 10:03</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserAuthMapper userAuthMapper;
    private final UserTokenMapper userTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    private final LogApplicationService logApplicationService;

    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO dto) {
        // 1. 创建基础用户记录
        User user = new User();
        // 此处 ID 建议由 MyBatis Plus 自动生成的雪花 ID 填充
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setStatus(1); // 默认正常
        userMapper.insert(user);

        // 2. 创建本地认证记录
        UserAuth auth = new UserAuth();
        auth.setUserId(user.getId());
        auth.setIdentityType("local"); // 本地账号密码登录
        auth.setIdentifier(dto.getUsername());
        // 密码必须通过 BCrypt 加密存储
        auth.setCredential(passwordEncoder.encode(dto.getPassword()));
        auth.setVerified(false); // 初始未验证
        userAuthMapper.insert(auth);
    }

    /**
     * 登陆成功后签发 JWT token
     *
     * @param dto 登陆信息
     * @return Token
     */
    public String login(UserLoginDTO dto) {
        // 1. 调用 Security 框架进行身份认证
        try {
            Authentication authentication;
            try {
                // 这行代码会触发 CustomUserDetailsService.loadUserByUsername 并校验密码
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
                );
            } catch (AuthenticationException e) {
                log.warn("用户登录失败: {}", e.getMessage());
                throw new ApiException("用户名或密码错误");
            }

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

            // 异步发送消息
            logApplicationService.sendLoginLogToMQ(user.getUserId(), user.getUsername(), true, "登陆成功");

            return handleLoginSuccess(user);
        } catch (Exception e) {
            // 即使失败也记录日志
            logApplicationService.sendLoginLogToMQ(null, dto.getUsername(), false, e.getMessage());
            throw e;
        }
    }

    private String handleLoginSuccess(CustomUserDetails user) {
        // 2. 认证成功，获取用户信息
        // 根据 identifier 获取内部真实的 userId (假设 identifier 就是用户名)
        UserAuth userAuth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getIdentifier, user.getUsername()));

        String userId = userAuth.getUserId().toString();

        // 3. 签发 JWT Token (包含 JTI)
        String token = JwtUtils.createToken(userId);
        String jti = JwtUtils.getJti(token);
        long expireInHours = 24;

        // 4. 【混合模式 - 第一写】写入 Redis 活性白名单
        String redisKey = "auth:token:" + userId + ":" + jti;
        redisTemplate.opsForValue().set(redisKey, "1", expireInHours, TimeUnit.HOURS);

        // 5. 【混合模式 - 第二写】写入 PostgreSQL 审计账本
        UserToken userToken = new UserToken();
        userToken.setUserId(Long.valueOf(userId));
        userToken.setJti(jti);
        userToken.setTokenVal(token);
        userToken.setExpireAt(OffsetDateTime.now().plusHours(expireInHours));
        userToken.setClientIp(IpUtils.getRemoteIp()); // 建议记录 IP
        userTokenMapper.insert(userToken);

        log.info("用户 {}({}) 登录成功，签发 JTI: {}", userAuth.getIdentifier(), userId, jti);
        return token;
    }


}
