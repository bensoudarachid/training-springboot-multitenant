package com.royasoftware.school;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.royasoftware.school.rest.CounterWebSocketHandler;

//@Component
public class CounterWebSocketScheduler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    CounterWebSocketHandler counterHandler;

//    @Scheduled(fixedRate = 60000)
    public void sendCounterUpdate() {
        counterHandler.counterIncrementedCallback(counter.incrementAndGet());
    }

}
