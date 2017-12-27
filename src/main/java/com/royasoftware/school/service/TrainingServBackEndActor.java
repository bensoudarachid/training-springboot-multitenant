package com.royasoftware.school.service;

import static akka.pattern.PatternsCS.pipe;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.royasoftware.school.model.Training;
import com.royasoftware.school.service.TrainingServFrEndActor.GetTrainings;
import com.royasoftware.school.service.TrainingServFrEndActor.Trainings;

//import sample.cluster.factorial.FactorialResult;


@Component("TrainingServBackEndActor")
@Scope("prototype")
public class TrainingServBackEndActor extends AkkaAppActor {
	static Logger logger = LogManager.getLogger(TrainingServBackEndActor.class.getName());

	@Autowired(required = false)
	private TrainingService trainingService;

	public TrainingServBackEndActor() {
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(AkkaAppMsg.class, this::generalMessageProcessor).match(String.class, msg -> {

			logger.info("Send Hallo to ");
			// Collection<Training> trainingColl
//			Training tr = trainingService.findById(1l);
//			logger.info("I got a training tr=" + tr);
			getSender().tell("Hey from TrainingServBackEndActor", getSelf());
		}).build();
	}

	private void generalMessageProcessor(AkkaAppMsg msg) {
		setTenantContext(msg);
		if (msg instanceof GetTrainings) {
			logger.debug("Get Training list on TrainingServBackEndActor");
			Collection<Training> trainingColl = trainingService.findAll();
			sender().tell(trainingColl, getSelf());
//			CompletableFuture<Collection<Training>> result = CompletableFuture.supplyAsync(() -> trainingService.findAll());
//			pipe(result, getContext().dispatcher()).to(sender());
		} else if (msg instanceof Message) {
			logger.info("BackEnd Got this message : " + ((Message) msg).getMessage()
					+ ". Sending Hey back.");
//			Collection<Training> trainingColl = trainingService.findAll();
//			sender().tell("Hey from TrainingServBackEndActor", getSelf());
//			sender().tell(new Trainings(trainingColl), getSelf());
			Training training = trainingService.findById(1l);
			sender().tell(training, getSelf());
		}
	}


}
