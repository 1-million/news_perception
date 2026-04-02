package com.qr.np.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

@Slf4j
public class MyPublisher implements Flow.Publisher<String>{

    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        MySubscription subscription = new MySubscription(subscriber);
        log.info("MyPublisher-subscribe");
        subscriber.onSubscribe(subscription);
    }
}
