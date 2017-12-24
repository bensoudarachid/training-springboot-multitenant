package com.royasoftware.service;

import java.io.Serializable;
import java.util.Collection;

//import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.royasoftware.TenantContext;
import com.royasoftware.model.Training;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

//@Named("UserRegistryActor")
@Component("TrainingServiceActor")
@Scope("prototype")
public class TrainingServiceActor extends AbstractActor {
	private static Logger logger = LoggerFactory.getLogger(TrainingServiceActor.class);
	@Autowired
	private ActorSystem actorSystem;
	Cluster cluster = null;

	ThreadLocal th = new ThreadLocal<>();
	public TrainingServiceActor() {	
		cluster = Cluster.get(getContext().getSystem());
//		logger.info("TrainingServiceActor cluster="+cluster); 
		th = TenantContext.getTenantContextThreadLocal();
		TenantContext.getCurrentTenant();
	}


	// LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	@Autowired(required = false)
	private TrainingService trainingService;

	static Props props() {
		return Props.create(TrainingServiceActor.class);
	}

	  @Override
	  public void preStart() {
	    cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), 
	        MemberEvent.class, UnreachableMember.class);
	  }

	  //re-subscribe when restart
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
		return receiveBuilder().match(GetTrainings.class, getTrainings -> {
			// logger.info("ctx="+ctx);
			// TrainingService trainingService =
			// ctx.getBean(TrainingService.class); //
			// ctx.getBean(TrainingService.class);
			// logger.info("trainingService="+trainingService);
//			logger.info("trainingService says : HI!!");
			Collection<Training> trainingColl = trainingService.findAll();
			getSender().tell(new Trainings(trainingColl), getSelf());
		}).match(MemberUp.class, mUp -> {
	        logger.info("Member is Up: {}", mUp.member());
	      })
	      .match(UnreachableMember.class, mUnreachable -> {
	    	  logger.info("Member detected as unreachable: {}", mUnreachable.member());
	      })
	      .match(MemberRemoved.class, mRemoved -> {
	    	  logger.info("Member is Removed: {}", mRemoved.member());
	      })
	      .match(MemberEvent.class, message -> {
	    	  logger.info("Member message ="+message); 
	        // ignore
	      }).build();
	}

	public static class GetTrainings {
	}

	public class Trainings implements Serializable {
		Collection<Training> trainings;

		public Trainings(Collection<Training> trainings) {
			this.trainings = trainings;
		}

		public Collection<Training> getTrainings() {
			return trainings;
		}

		public void setTrainings(Collection<Training> trainings) {
			this.trainings = trainings;
		}

	}

	public class ActionPerformed implements Serializable {
		private final String description;

		public ActionPerformed(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public class CreateTraining implements Serializable {
		private final Training training;

		public CreateTraining(Training training) {
			this.training = training;
		}

		public Training getTraining() {
			return training;
		}
	}


	public class DeleteTraining implements Serializable {
		private final Training training;

		public DeleteTraining(Training training) {
			this.training = training;
		}

		public Training getTraining() {
			return training;
		}
	}
}
