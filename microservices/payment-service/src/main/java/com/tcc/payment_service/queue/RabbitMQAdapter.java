package com.tcc.payment_service.queue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

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

    public RabbitMQAdapter(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to connect RabbitMQ: " + e.getMessage());
        }
    }

    @Override
    public void close() {
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
    public void publish(String exchange, Object data) {
        try {
            String message = objectMapper.writeValueAsString(data);
            channel.basicPublish(exchange, "", null, message.getBytes(StandardCharsets.UTF_8));
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

}
