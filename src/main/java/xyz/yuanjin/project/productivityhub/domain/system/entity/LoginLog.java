package xyz.yuanjin.project.productivityhub.domain.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 16:51</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
@Setter
@TableName("sys_login_log")
public class LoginLog {
    @TableId
    private Long id;
    private Long userId;
    private String username;
    private String ipAddress;
    private String loginLocation; // 位置信息
    private String browser; // 浏览器信息
    private String os; // 操作系统
    private Integer status; // 状态：1-成功, 0-失败
    private String msg; // 提示消息（如密码错误）
    private OffsetDateTime loginTime;
    private OffsetDateTime createTime; // 记录创建时间
}
