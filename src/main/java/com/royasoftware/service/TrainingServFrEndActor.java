package com.royasoftware.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

//import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.royasoftware.TenantContext;
import com.royasoftware.model.Training;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.routing.FromConfig;

//@Named("UserRegistryActor")
@Component("TrainingServFrEndActor")
@Scope("prototype")
public class TrainingServFrEndActor extends AkkaAppActor {
	private static Logger logger = LoggerFactory.getLogger(TrainingServFrEndActor.class);
	@Autowired
	private ActorSystem actorSystem;
	Cluster cluster = null;
	ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "trainingServBackEndRouter");

	ThreadLocal th = new ThreadLocal<>();

	public TrainingServFrEndActor() {
		cluster = Cluster.get(getContext().getSystem());
		th = TenantContext.getTenantContextThreadLocal();
		TenantContext.getCurrentTenant();
	}

	// LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	@Autowired(required = false)
	private TrainingService trainingService;

	static Props props() {
		return Props.create(TrainingServFrEndActor.class);
	}

	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}

	// re-subscribe when restart
	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	// @Autowired(required = false)
	// private TrainingDAO trainingDao;

	// @Autowired(required = false)
	// private ApplicationContext ctx;
	@Override
	public Receive createReceive() {

		return receiveBuilder().match(AkkaAppMsg.class, this::generalMessageProcessor)
//				.match(String.class, message -> {
//			logger.info("FrontEnd Got this message : " + message + ". Sending it to backend. Parent is the sender");
//			backend.tell("Msg to FatorialBackend!", getSender());
//			// getSender().tell("Hi Back!", getSelf());
//			// backend.tell("Hi Back!", self());
//		})
		.match(MemberUp.class, mUp -> {
			logger.info("Member is Up: {}", mUp.member());
		}).match(UnreachableMember.class, mUnreachable -> {
			logger.info("Member detected as unreachable: {}", mUnreachable.member());
		}).match(MemberRemoved.class, mRemoved -> {
			logger.info("Member is Removed: {}", mRemoved.member());
		}).match(MemberEvent.class, message -> {
			logger.info("Member message =" + message);
			// ignore
		}).build();
	}
	private void generalMessageProcessor(AkkaAppMsg msg) {
//		setTenantContext(msg);
//		HashMap<String, Object> tenantTL = msg.getOriginalThreadHashMap();
//		logger.info("tenantTL 2.get(tenant)=" + tenantTL.get("tenant"));
//		TenantContext.setTenantContextThreadLocalMap(msg.getOriginalThreadHashMap());
		if( msg instanceof GetTrainings){
//			 Collection<Training> trainingColl = trainingService.findAll();
//			 getSender().tell(new Trainings(trainingColl), getSelf());

//			logger.info("sent getTrainings to FatorialBackend = " + backend);
			backend.tell(msg, getSender());			
		
		} else if(msg instanceof Message){
			logger.info("FrontEnd Got this message : " + ((Message)msg).getMessage() + ". Sending it to backend. Parent is the sender");
			backend.tell(msg, getSender());			
		}
	}

	
	public static class GetTrainings implements AkkaAppMsg {
	}

	public static class Trainings implements AkkaAppMsg{
		Vector<Training> trainings;

		public Trainings(Collection<Training> trainings) {
			this.trainings = new Vector(trainings);
		}

		public Vector<Training> getTrainings() {
			return trainings;
		}

//		public void setTrainings(Collection<Training> trainings) {
//			this.trainings = trainings;
//		}

	}

	public static class ActionPerformed {
		private final String description;

		public ActionPerformed(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public static class CreateTraining {
		private final Training training;

		public CreateTraining(Training training) {
			this.training = training;
		}

		public Training getTraining() {
			return training;
		}
	}

	public static class DeleteTraining {
		private final Training training;

		public DeleteTraining(Training training) {
			this.training = training;
		}

		public Training getTraining() {
			return training;
		}
	}
}
