package com.royasoftware.service;

import static akka.pattern.PatternsCS.pipe;

import java.util.Collection;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.royasoftware.model.Training;
import com.royasoftware.service.TrainingServFrEndActor.GetTrainings;
import com.royasoftware.service.TrainingServFrEndActor.Trainings;

import sample.cluster.stats.StatsWorker;

@Component("TrainingServBackEndActor")
@Scope("prototype")
public class TrainingServBackEndActor extends AkkaAppActor {
	static Logger logger = LogManager.getLogger(StatsWorker.class.getName());

	@Autowired(required = false)
	private TrainingService trainingService;

	public TrainingServBackEndActor() {
		logger.info("###########################################TrainingServBackEndActor constructor "
				+ new Random().nextInt());
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
		// HashMap<String, Object> tenantTL = msg.getOriginalThreadHashMap();
		// logger.info("tenantTL 2.get(tenant)=" + tenantTL.get("tenant"));
		// TenantContext.setTenantContextThreadLocalMap(msg.getOriginalThreadHashMap());
		if (msg instanceof GetTrainings) {
			logger.info("TrainingServBackEndActor. Get Training list ");
			Collection<Training> trainingColl = trainingService.findAll();
			logger.info("TrainingServBackEndActor. trainingColl size ="+trainingColl.size()); 
			logger.info("Send it to controller");
//			pipe(result, getContext().dispatcher()).to(sender());
			sender().tell(trainingColl, getSelf());
//			sender().tell("Hey from TrainingServBackEndActor", getSelf());
			

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
