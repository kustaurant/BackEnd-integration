package com.kustaurant.crawler.aianalysis.adapter.out.messaging;

import java.util.Optional;
import java.util.function.Consumer;

public interface MessageReader {

    <T> Optional<Message<T>> read(String topic, String group, String consumerName, Class<T> type);
}
