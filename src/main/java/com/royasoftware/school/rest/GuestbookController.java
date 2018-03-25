package com.royasoftware.school.rest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GuestbookController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
			    
	
	/**
	 * A simple DTO class to encapsulate messages along with their timestamps.
	 */
	public static class MessageDTO {
		public String time;
		public String content;
		static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
		
		public MessageDTO() {
	        super();
	    }
	 
	    public MessageDTO(String content, String time) {
	        this.time = time;
	        this.content = content;
	    }
		public MessageDTO(String message) {
			this.time = message+" : "+dateFormatter.format(new Date(System.currentTimeMillis()));
			this.content = message;
		}
	}

	/**
	 * Listens the /app/guestbook endpoint and when a message is received,
	 * encapsulates it in a MessageDTO instance and relays the resulting object
	 * to the clients listening at the /topic/entries endpoint.
	 * 
	 * @param message
	 *            the message
	 * @return the encapsulated message
	 */
	// @MessageMapping("/api/guestbook")
	// @SendTo("/topic/entries")
	@MessageMapping("/exchange/${jsa.rabbitmq.exchange}")
	@SendTo("/queue/${jsa.rabbitmq.sendqueue}")

	public MessageDTO guestbook(String message) {
		logger.info("Received message: " + message);
		return new MessageDTO(message);
	}

}
