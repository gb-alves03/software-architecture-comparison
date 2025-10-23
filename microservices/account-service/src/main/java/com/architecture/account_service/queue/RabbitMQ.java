package com.architecture.account_service.queue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQ {

    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;

    private static final String DEFAULT_EXCHANGE = "payment.exchange";
    private static final String DEFAULT_QUEUE = "payment.done.queue";
    private static final String DEFAULT_ROUTING_KEY = "payment.done";

    public RabbitMQ(RabbitAdmin rabbitAdmin, RabbitTemplate rabbitTemplate) {
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitTemplate = rabbitTemplate;

        DirectExchange exchange = new DirectExchange(DEFAULT_EXCHANGE, true, false);
        Queue queue = new Queue(DEFAULT_QUEUE, true);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(DEFAULT_ROUTING_KEY);

        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }

    public void send(String exchange, String routingKey, Object payload) {
        String ex = (exchange == null) ? DEFAULT_EXCHANGE : exchange;
        String rk = (routingKey == null) ? DEFAULT_ROUTING_KEY : routingKey;
        rabbitTemplate.convertAndSend(ex, rk, payload);
    }

    public void declare(String exchangeName, String queueName, String routingKey) {
        DirectExchange exchange = new DirectExchange(exchangeName, true, false);
        Queue queue = new Queue(queueName, true);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }
}
