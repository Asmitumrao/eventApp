package com.veersa.eventApp.messaging;

import com.veersa.eventApp.DTO.RefundMessage;
import com.veersa.eventApp.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendRefund(RefundMessage refundMessage) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REFUND_EXCHANGE,
                RabbitMQConfig.REFUND_ROUTING_KEY,
                refundMessage
        );
//        System.out.println("Refund message sent: " + refundMessage);
    }
}
