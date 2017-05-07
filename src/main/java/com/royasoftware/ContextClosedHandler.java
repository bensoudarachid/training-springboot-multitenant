package com.royasoftware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	@Autowired ThreadPoolTaskExecutor executor;
//    @Autowired RouterMonitor scheduler;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
    	logger.info("onApplicationEvent close event"); 
//        scheduler.shutdown();
//        executor.shutdown();
    }       
}