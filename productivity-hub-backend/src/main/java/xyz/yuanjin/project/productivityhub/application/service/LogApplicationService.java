package xyz.yuanjin.project.productivityhub.application.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.yuanjin.project.productivityhub.api.dto.UserLoginDTO;
import xyz.yuanjin.project.productivityhub.application.dto.mq.LoginLogMessage;
import xyz.yuanjin.project.productivityhub.common.util.IpUtils;
import xyz.yuanjin.project.productivityhub.domain.user.entity.CustomUserDetails;
import xyz.yuanjin.project.productivityhub.infrastructure.mq.producer.LoginLogProducer;

import java.time.OffsetDateTime;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 17:14</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class LogApplicationService {

    private final LoginLogProducer loginLogProducer;
    private final HttpServletRequest request;

    public void sendLoginLogToMQ(Long userId, String username, boolean success, String msg) {
        // 认证成功后，构建消息对象
        LoginLogMessage logMessage = LoginLogMessage.builder()
                .userId((userId))
                .username(username)
                .ip(IpUtils.getRemoteIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .status(success ? 1 : 0)
                .msg(msg)
                .loginTime(OffsetDateTime.now())
                .build();

        // 异步发送消息，直接返回，不等待数据库落库
        loginLogProducer.sendLoginLog(logMessage);
    }
}
