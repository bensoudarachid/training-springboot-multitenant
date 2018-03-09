package com.royasoftware.school.settings.school.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.royasoftware.school.rest.CounterWebSocketHandler;

//@Configuration
//@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    CounterWebSocketHandler counterHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(counterHandler, "/counter").setAllowedOrigins("*");
        registry.addHandler(counterHandler, "/api/guestbook").setAllowedOrigins("*").withSockJS();
	}
}
