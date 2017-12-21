package sample.cluster.simple;

import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

import javax.inject.Inject;
import javax.inject.Named;

//import javax.inject.Inject;
//import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.royasoftware.service.TrainingService;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@Named("MyCountingActor")
@Scope("prototype")
public class CountingActor extends AbstractActor {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public static class Count {
	}

	public static class Get {
	}

	// the service that will be automatically injected
	final CountingService countingService;
	@Autowired
	private TrainingService trainingService;

	@Inject
	public CountingActor(@Named("CountingService") CountingService countingService) {
		this.countingService = countingService;
	}

	private int count = 0;

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Count.class, mUp -> {
			count = countingService.increment(count);
			logger.info("trainingService.findAll()="+trainingService.findById(1l));
		}).match(Get.class, mUnreachable -> {
			getSender().tell(count, getSelf());
		}).build();
	}

	// @Override
	// public void onReceive(Object message) throws Exception {
	// if (message instanceof Count) {
	// count = countingService.increment(count);
	// } else if (message instanceof Get) {
	// getSender().tell(count, getSelf());
	// } else {
	// unhandled(message);
	// }
	// }
}
