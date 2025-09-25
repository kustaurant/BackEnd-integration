package com.kustaurant.crawler.infrastructure.messaging;

public interface MessagePublisher<T> {

    void publish(String topic, T payload);
}
