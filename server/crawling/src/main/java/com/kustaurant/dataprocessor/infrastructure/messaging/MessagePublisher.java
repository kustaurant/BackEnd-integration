package com.kustaurant.dataprocessor.infrastructure.messaging;

public interface MessagePublisher<T> {

    void publish(String topic, T payload);
}
