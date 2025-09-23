package com.kustaurant.dataprocessor.infrastructure.messaging;

import java.util.function.Consumer;

public interface MessageSubscriber {

    <T> void subscribe(String topic, String group, String consumerName, Class<T> type, Consumer<T> handler);

    void unsubscribeAll();
}
