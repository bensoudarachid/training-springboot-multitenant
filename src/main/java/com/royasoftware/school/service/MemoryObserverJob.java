package com.royasoftware.school.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.royasoftware.school.model.MemoryInfo;

@Service
public class MemoryObserverJob {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public final ApplicationEventPublisher eventPublisher;

	public MemoryObserverJob(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

//	@Scheduled(fixedRate = 6000)
	public void doSomething() {
		MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = memBean.getHeapMemoryUsage();
		MemoryUsage nonHeap = memBean.getNonHeapMemoryUsage();

		MemoryInfo mi = new MemoryInfo(heap.getUsed(), nonHeap.getUsed());
//		logger.info("send memory usage");
		this.eventPublisher.publishEvent(mi);
	}

}