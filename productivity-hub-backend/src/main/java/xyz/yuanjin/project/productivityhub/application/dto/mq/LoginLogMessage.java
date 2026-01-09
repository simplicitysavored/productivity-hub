package xyz.yuanjin.project.productivityhub.application.dto.mq;

import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 16:47</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginLogMessage implements Serializable {
    private Long userId;
    private String username;
    private String ip;
    private String userAgent; // 包含浏览器和系统信息
    private Integer status;
    private String msg;
    private OffsetDateTime loginTime;
}
