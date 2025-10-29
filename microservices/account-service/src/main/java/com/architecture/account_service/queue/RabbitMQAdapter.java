package com.architecture.account_service.queue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class RabbitMQAdapter implements Queue {
    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
    private int port;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;

    private Connection connection;
    private Channel channel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void connect() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(5000);
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to connect RabbitMQ: " + e.getMessage());
        }
    }

    private void close() {
        try {
            if (channel != null && channel.isOpen())
                channel.close();
            if (connection != null && connection.isOpen())
                connection.close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to close connection RabbitMQ: " + e.getMessage());
        }
    }

    @Override
    public void publish(String exchange, String routingKey, Object data) {
        try {
            String message = objectMapper.writeValueAsString(data);
            channel.basicPublish(exchange, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to publish message: " + e.getMessage());
        }
    }

    @Override
    public void consume(String queue, Function<String, Void> callback) {
        try {
            channel.basicConsume(queue, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                callback.apply(message);
            }, consumerTag -> {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to consume message: " + e.getMessage());
        }
    }

    @Override
    public void createExchange(String exchange, String type) {
        try {
            channel.exchangeDeclare(exchange, type, true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create exchange: " + e.getMessage());
        }
    }

    @Override
    public void createQueue(String queue) {
        try {
            channel.queueDeclare(queue, true, false, false, null);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create queue: " + e.getMessage());
        }
    }

    @Override
    public void bindingQueue(String queue, String exchange, String routingKey) {
        try {
            channel.queueBind(queue, exchange, routingKey);
        } catch (IOException e) {
            throw new RuntimeException("Failed to bind queue: " + e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        connect();
    }

    @PreDestroy
    public void shutdown() {
        close();
    }
}
