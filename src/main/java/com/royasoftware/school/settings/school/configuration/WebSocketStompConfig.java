package com.royasoftware.school.settings.school.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.royasoftware.school.rest.CounterWebSocketHandler;

//@Configuration
//@EnableWebSocketMessageBroker
public class WebSocketStompConfig extends AbstractWebSocketMessageBrokerConfigurer {

	// @Autowired
	// CounterWebSocketHandler counterHandler;
	@Value("${jsa.rabbitmq.exchange}")
	private String exchange;

	@Value("${jsa.rabbitmq.queue}")
	private String queue;

	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// use the /topic prefix for outgoing WebSocket communication
		// config.enableSimpleBroker("/topic");
		config.enableStompBrokerRelay("/topic", "/queue")
			.setRelayHost("192.168.99.100")
//			.setRelayPort(5672)
			.setRelayPort(31672)
			.setClientLogin("guest")
			.setClientPasscode("guest");
		// use the /app prefix for others
		// config.setApplicationDestinationPrefixes("/api");

	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// use the /guestbook endpoint (prefixed with /app as configured above)
		// for incoming requests
		// registry.addEndpoint("/guestbook").setAllowedOrigins("*.school.royasoftware.com").withSockJS();
//		registry.addEndpoint("/api/guestbook").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/"+exchange).setAllowedOrigins("*").withSockJS();
	}

}