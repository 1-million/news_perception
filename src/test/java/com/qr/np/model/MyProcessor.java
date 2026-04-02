package com.qr.np.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

@Slf4j
public class MyProcessor implements Flow.Processor<String,String>{
    StringBuilder data = new StringBuilder();
    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        MySubscription subscription = new MySubscription(subscriber);
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        MySubscription mySubscription = (MySubscription) subscription;
        mySubscription.request(2);
    }

    @Override
    public void onNext(String item) {
        System.out.println("MyProcessor-onNext:"+item);
        data.append(item);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {
        log.info("MyProcessor-onComplete:{}",data.toString());
    }
}
