package com.royasoftware.school.cluster;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.royasoftware.school.AkkaSpringConfig;

import akka.actor.ActorSystem;

public class AkkaSystemStarter {
	private static Logger logger = LoggerFactory.getLogger(AkkaSystemStarter.class);

	public static void main(String[] args) {
		logger.info("Akka Server starts");
		Properties props = System.getProperties();
		props.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(AkkaSpringConfig.class);
		ctx.refresh();

		ActorSystem actorSystem = ctx.getBean(ActorSystem.class);
		SpringExtension springExtension = ctx.getBean(SpringExtension.class);

		actorSystem.actorOf(springExtension.props("TrainingServBackEndActor"), "trainingServBackEndActor");
		logger.info("Akka Server up");
		try {
			System.in.read(); // let it run until user presses return
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
