package xyz.yuanjin.project.productivityhub.infrastructure.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import xyz.yuanjin.project.productivityhub.application.dto.mq.LoginLogMessage;
import xyz.yuanjin.project.productivityhub.bootstrap.config.RabbitConfig;
import xyz.yuanjin.project.productivityhub.domain.system.entity.LoginLog;
import xyz.yuanjin.project.productivityhub.infrastructure.repository.LoginLogMapper;

import java.io.IOException;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 16:50</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginLogConsumer {
    private final LoginLogMapper loginLogMapper;

    @RabbitListener(queues = RabbitConfig.LOGIN_LOG_QUEUE_NAME)
//    public void handleLoginLog(LoginLogMessage logMsg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
    public void handleLoginLog(LoginLogMessage logMsg) {
        try {
            log.info("异步记录登录日志: {}", logMsg.getUsername());

            // 1. DTO 转 实体 (可以使用 MapStruct)
            LoginLog entity = new LoginLog();
            BeanUtils.copyProperties(logMsg, entity);

            // 2. 存入数据库
            loginLogMapper.insert(entity);

            log.info("异步记录登录日志成功");
        } catch (Exception e) {
            log.error("异步记录登录日志失败: {}", e.getMessage());
            throw e;
        }
    }
}
