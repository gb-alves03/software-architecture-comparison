package com.tcc.payment_service.queue;

import java.util.function.Function;

public interface Queue {
    void connect();

    void close();

    void publish(String exchange, Object data);

    void consume(String queue, Function<String, Void> callback);
}
