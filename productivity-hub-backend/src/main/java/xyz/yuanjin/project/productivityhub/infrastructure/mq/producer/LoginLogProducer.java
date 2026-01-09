package xyz.yuanjin.project.productivityhub.infrastructure.mq.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import xyz.yuanjin.project.productivityhub.application.dto.mq.LoginLogMessage;
import xyz.yuanjin.project.productivityhub.bootstrap.config.RabbitConfig;

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
public class LoginLogProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendLoginLog(LoginLogMessage message) {
        // 使用之前配置好的交换机和路由键
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.LOGIN_LOG_ROUTING_KEY,
                message
        );
    }
}
