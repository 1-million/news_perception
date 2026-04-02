package com.qr.np;

import com.qr.np.model.MyProcessor;
import com.qr.np.model.MyPublisher;
import com.qr.np.model.MySubscriber;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxStreamTests {

    @Test
    public void testFluxStream() {
        System.out.println("testFluxStream");
        MyPublisher publisher = new MyPublisher();
        MySubscriber subscriber = new MySubscriber();
        MyProcessor processor = new MyProcessor();
        publisher.subscribe(processor);
        processor.subscribe(subscriber);

    }

    @Test
    public void testFluxStream2() {
        Flux<Integer> flux = Flux.just(1,2,3,4,5,6,7,8,9,10);
        flux = flux.log();
        flux.subscribe(System.out::println);
        flux = flux.map(i->i*2).log();
        flux.subscribe(System.out::println);
    }
}
