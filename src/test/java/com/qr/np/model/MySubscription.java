package com.qr.np.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow;

@Slf4j
public class MySubscription implements Flow.Subscription{
    private List<String> data = Arrays.asList("1","2","3","4","5");
    private Flow.Subscriber<? super String> subscriber;
    public MySubscription(Flow.Subscriber<? super String> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {
        log.info("单次需要的数据量:{}",n);
        double total = data.size();
        int pages = (int)Math.ceil(total/n);
        for (int i = 0; i < pages; i++) {
            int start = i*(int)n;
            int end = (i+1)*(int)n;
            if(end>total){
                end = (int)total;
            }
            subscriber.onNext(data.subList(start,end).toString());
        }
        subscriber.onComplete();
    }

    @Override
    public void cancel() {

    }
}
