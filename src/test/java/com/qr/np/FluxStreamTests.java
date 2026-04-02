package com.qr.np;

import com.qr.np.model.MyPublisher;
import com.qr.np.model.MySubscriber;
import com.qr.np.model.MySubscription;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

public class FluxStreamTests {

    @Test
    public void testFluxStream() {
        System.out.println("testFluxStream");
        MyPublisher publisher = new MyPublisher();
        MySubscriber subscriber = new MySubscriber();
        publisher.subscribe(subscriber);

    }
}
