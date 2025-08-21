package com.veersa.eventApp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REFUND_QUEUE = "refund-queue";
    public static final String REFUND_EXCHANGE = "refund-exchange";
    public static final String REFUND_ROUTING_KEY = "refund.key";


    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue refundQueue() {
        return new Queue(REFUND_QUEUE, true);
    }

    @Bean
    public TopicExchange refundExchange() {
        return new TopicExchange(REFUND_EXCHANGE);
    }

    @Bean
    public Binding refundBinding(Queue refundQueue, TopicExchange refundExchange) {
        return BindingBuilder.bind(refundQueue).to(refundExchange).with(REFUND_ROUTING_KEY);
    }
}
