package com.royasoftware.school.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.royasoftware.school.rest.GuestbookController.MessageDTO;
 
@Component
public class RabbitConsumer {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObjectMapper objectMapper= new ObjectMapper();
	
///	@RabbitListener(queues="jsa.sendqueue")
//    public void receivedMessage(String message) {
//    public void receivedMessage(MessageDTO message) {
//    public void receivedMessage(byte[] message) {
//    public void receivedMessage(Message message) {
//		try {
//			logger.info("Received Message "+message.getBody().toString());
//			String s = new String(message.getBody());
//			logger.info("Received Message "+s);
//			MessageDTO messageDTO = objectMapper.readValue(s, MessageDTO.class);
//			logger.info("messageDTO="+messageDTO.content);
////			logger.info("here is is : " + (MessageDTO)SerializationUtils.deserialize(message));			
////			logger.info("here is is : " + SerializationUtils.deserialize(message));			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		logger.info("here is is : " + SerializationUtils.deserialize(msg));
////		MessageDTO s = (MessageDTO)SerializationUtils.deserialize(msg);
//    }
    public void receivedMessage(MessageDTO messageDTO) {
		try {
			logger.info("Rabbit Consumer messageDTO="+messageDTO.content);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
