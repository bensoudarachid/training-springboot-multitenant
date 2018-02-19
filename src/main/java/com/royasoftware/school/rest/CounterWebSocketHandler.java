package com.royasoftware.school.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class CounterWebSocketHandler extends TextWebSocketHandler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    WebSocketSession session;

    // This will send only to one client(most recently connected)
    public void counterIncrementedCallback(int counter) {
        if (session != null && session.isOpen()) {
            try {
                logger.debug("Now sending:" + counter);
                session.sendMessage(new TextMessage("{\"value\": \"" + counter + "\"}"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        	logger.debug("Don't have open session to send:" + counter);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
    	logger.debug("Connection established");
        this.session = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        logger.info("message="+message); 
        if ("CLOSE".equalsIgnoreCase(message.getPayload())) {
            session.close();
        } else {
        	logger.debug("Received:" + message.getPayload());
        }
    }
}
