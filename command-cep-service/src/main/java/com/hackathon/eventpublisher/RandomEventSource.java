package com.hackathon.eventpublisher;

import com.common.model.Event;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomEventSource implements SourceFunction<Event> {
    private final int count;
    private final long initialTimestamp;

    private volatile boolean isRunning = true;
    private volatile AtomicInteger number = new AtomicInteger(0);
    private volatile long closeDelayTimestamp = 1000;

    private double double1 = 0D;
    private double double2 = 0D;

    private final Random random = new Random();


    public RandomEventSource(int count, long initialTimestamp) {
        this.count = count;
        this.initialTimestamp = initialTimestamp;
    }

    public RandomEventSource() {
        this(Integer.MAX_VALUE, System.currentTimeMillis());
    }

    public RandomEventSource(int count) {
        this(count, System.currentTimeMillis());
    }

    public RandomEventSource closeDelay(long delayTimestamp) {
        this.closeDelayTimestamp = delayTimestamp;
        return this;
    }

    @Override
    public void run(SourceContext<Event> ctx) throws Exception {
        while (isRunning) {
            Thread.sleep(1000);
            long timestamp = initialTimestamp + 1000 * number.get();
            double1 += 1;
            Event event = new Event();
            int tmp = 5;
            String[] sourceFilters = {"aws", "qualys", "trendmicro", "graph", "box"};
            for(int i=0; i< 150000; i++) {
                if (i==tmp){
                    break;
                }
                timestamp = timestamp + 5;
                int randomNumber = random.nextInt(100000) + 1;
                event.setEventId(randomNumber);
                event.setDstIp4("1.2.3.4");
                event.setDataSource(sourceFilters[random.nextInt(5)]);
                ctx.collectWithTimestamp(event, timestamp);
            }
            if (number.incrementAndGet() >= this.count) {
                cancel();
            }
        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
        try {
            Thread.sleep(closeDelayTimestamp);
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
