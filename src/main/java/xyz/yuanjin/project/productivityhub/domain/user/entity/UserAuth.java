package xyz.yuanjin.project.productivityhub.domain.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 09:36</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
@Setter
@TableName("user_auths")
public class UserAuth {
    @TableId
    private Long id;

    private Long userId;                  // 关联 users 表的 id

    private String identityType;          // 认证类型：'local', 'github', 'google'

    private String identifier;            // 标识：本地存用户名，第三方存 OpenID

    private String credential;            // 凭证：本地存加密密码

    private Boolean verified;             // 是否已验证

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;
}
