package com.kustaurant.crawler.aianalysis.adapter.out.messaging;

import java.util.List;

public interface MessagePublisher<T> {

    void publish(String topic, T payload);

    void publish(String topic, List<T> payloads);
}
