package xyz.yuanjin.project.productivityhub.domain.user.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 17:01</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long userId; // 核心：保存数据库里的用户主键 ID

    public CustomUserDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }
}
