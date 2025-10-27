package com.architecture.account_service.queue;

import java.util.function.Function;

public interface Queue {

    void publish(String exchange, String routingKey, Object data);

    void consume(String queue, Function<String, Void> callback);

    void createExchange(String exchange, String type);

    void createQueue(String queue);

    void bindingQueue(String queue, String exchange, String routingKey);
}
