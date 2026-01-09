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
 * <p>创建日期：2026/1/4 14:43</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
@Setter
@TableName("user_tokens")
public class UserToken {
    @TableId
    private Long id;

    private Long userId; // 用户 ID
    private String jti; // JWT 唯一标识
    private String tokenVal; // 完整的 Token (可选)
    private String clientIp; // 登录 IP
    private OffsetDateTime expireAt; // 过期时间
    private Boolean isRevoked; // 是否手动撤销

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;     // 创建时间

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;     // 更新时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;
}
