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
 * <p>创建日期：2026/1/4 09:35</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
@Setter
@TableName("users")
public class User {
    @TableId
    private Long id;                      // 用户ID（建议雪花算法）

    private String nickname;              // 用户昵称

    private String avatarUrl;             // 头像路径

    private String email;                 // 绑定邮箱

    private Integer status;               // 状态：1-正常, 0-禁用

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;     // 创建时间

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;     // 更新时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;
}
