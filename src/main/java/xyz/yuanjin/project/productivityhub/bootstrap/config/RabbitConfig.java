package xyz.yuanjin.project.productivityhub.bootstrap.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 16:33</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "ph.business.exchange";
    public static final String LOGIN_LOG_QUEUE_NAME = "ph.login.log.quorum.queue";
    public static final String LOGIN_LOG_ROUTING_KEY = "ph.login.log.routing";

    // --- 死信配置 (补偿逻辑) ---
    public static final String DEAD_LETTER_EXCHANGE = "ph.dead.exchange";
    public static final String DEAD_LETTER_QUEUE = "ph.dead.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "ph.dead.routing";

    // ================== 1. 声明死信队列相关 (处理失败的消息) ==================

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    // 开启此项后，生产者和消费者都会自动切换为 JSON 模式。
    @Bean
    public MessageConverter jsonMessageConverter() {
        // 强制使用 Jackson 将对象转为 JSON 字符串存储
        return new Jackson2JsonMessageConverter();
    }

    // 1. 定义 Direct 交换机
    @Bean
    public DirectExchange businessExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    // 2. 定义仲裁队列 (Quorum Queue)
    @Bean
    public Queue quorumQueue() {
        return QueueBuilder.durable(LOGIN_LOG_QUEUE_NAME)
                .quorum() // 关键：声明这是一个仲裁队列
                // 关键点：当消息变为死信，发往哪个交换机
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                // 关键点：死信消息发往交换机时使用的 RoutingKey
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                // 可选：设置消息在队列里的生存时间 (10秒没处理就变死信)
                // .ttl(10000)
                .build();
    }

    // 3. 绑定交换机与队列
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(quorumQueue())
                .to(businessExchange())
                .with(LOGIN_LOG_ROUTING_KEY);
    }
}
