package org.example.xpaymentadapter.configuration;


import lombok.AllArgsConstructor;
import org.example.xpaymentadapter.configuration.properties.RabbitProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitConfig {
    private final RabbitProperties rabbitProperties;

    @Bean
    public Queue xpaymentApiDlq() {
        return new Queue(rabbitProperties.routingKey(), true); // durable queue
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
