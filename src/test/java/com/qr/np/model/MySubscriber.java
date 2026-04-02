package com.qr.np.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

@Slf4j
public class MySubscriber implements Flow.Subscriber<String>{
    private Flow.Subscription subscription;
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        log.info("MySubscriber-subscribe");
        log.info("MySubscriber-subscription;{}",subscription);
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        log.info("MySubscriber-onNext:{}",item);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {
        log.info("MySubscriber-onComplete.");
    }
}
