package xyz.yuanjin.project.productivityhub.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xyz.yuanjin.project.productivityhub.domain.user.entity.CustomUserDetails;
import xyz.yuanjin.project.productivityhub.domain.user.entity.UserAuth;
import xyz.yuanjin.project.productivityhub.infrastructure.repository.UserAuthMapper;

import java.util.ArrayList;

/**
 * <p>标题： 用户领域的身份建模和权限判定 </p >
 * <p>功能： 实现 UserDetailsService 接口，根据 user_auths 表加载用户信息。</p >
 * <p>创建日期：2026/1/4 09:30</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserAuthMapper userAuthMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("安全认证 | {} | loadUserByUsername...",  username);

        // 1. 从 user_auths 表中根据标识符（用户名/邮箱等）查询
        UserAuth auth = userAuthMapper.selectOne(
                new LambdaQueryWrapper<UserAuth>()
                        .eq(UserAuth::getIdentifier, username)
        );
        // 2. 如果没查到，抛出异常
        if (auth == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 3. 返回 Spring Security 预置的 User 对象
        // 注意：这里的 auth.getCredential() 应该是加密后的密码（BCrypt）
        return new CustomUserDetails(
                auth.getUserId(),
                auth.getIdentifier(),
                auth.getCredential(),
                new ArrayList<>() // 暂时给一个空的权限列表，后续可从数据库加载角色
        );
    }
}
