package com.kustaurant.crawler.aianalysis.adapter.out.messaging;

public record Message<T>(
        T payload,
        Runnable doAck
) {

}
