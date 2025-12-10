package org.example.xpaymentadapter.rabbit;

import lombok.AllArgsConstructor;
import org.example.xpaymentadapter.configuration.properties.RabbitProperties;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class RabbitPaymentMessageService {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    public void send(XPaymentApiRequest payload) {
        rabbitTemplate.convertAndSend(rabbitProperties.routingKey(), payload);
    }

    public XPaymentApiRequest receiveOneMessage() {
        Object message = rabbitTemplate.receiveAndConvert(rabbitProperties.routingKey());
        return message instanceof XPaymentApiRequest ? (XPaymentApiRequest) message : null;
    }
}
