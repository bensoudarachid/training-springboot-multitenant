package com.royasoftware.school;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.royasoftware.school.model.Training;
import com.royasoftware.school.rest.GuestbookController.MessageDTO;

@Component
public class CounterWebSocketScheduler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private AtomicInteger counter = new AtomicInteger(0);
	private List<Training> trainingList;
	private HashSet<String> activeTenantList = new HashSet<String>();

	// @Autowired
	// private SimpMessagingTemplate template;

	@Autowired
	private AmqpTemplate amqpTemplate;

//	@Value("${jsa.rabbitmq.exchange}")
//	private String exchange;
//
//	@Value("${jsa.rabbitmq.queue}")
//	private String queue;

	@Value("${jsa.rabbitmq.routingkey}")
	private String routingKey;

	@Scheduled(fixedRate = 5000)
	public void publishUpdates() {
		// logger.info("Here i am now "+TenantContext.getValidTenants());
		// template.convertAndSend("/topic/entries", new MessageDTO("YES" ));
		// template.convertAndSend("/"+queue, new MessageDTO("YES"));
//		TenantContext.getValidTenants()
		activeTenantList.stream().forEach(tenant -> {
//			logger.info("Here i am now "+tenant);
			amqpTemplate.convertAndSend("amq.topic", routingKey + "." + tenant, new MessageDTO(tenant));
		});

		// amqpTemplate.convertAndSend("amq.topic", routingKey+".1245534", new
		// MessageDTO("YES" ));
	}

	@RabbitListener(queues = "jsa.sendqueue")
	public void receivedMessage(MessageDTO messageDTO) {
		try {
			String tenant = messageDTO.content;
			if(!tenant.endsWith(".school.royasoftware.com"))
				return;
			tenant = tenant.substring(0, tenant.indexOf(".school.royasoftware.com"));
			if( tenant.contains("."))
				return;
			activeTenantList.add(tenant);
			logger.info("Scheduler listener messageDTO=" + tenant);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
