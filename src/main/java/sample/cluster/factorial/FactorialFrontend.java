package sample.cluster.factorial;

import static akka.pattern.PatternsCS.pipe;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import scala.concurrent.duration.Duration;

public class FactorialFrontend extends AbstractActor {
	static Logger logger = LogManager.getLogger(FactorialFrontend.class.getName());
	final int upToN;
	final boolean repeat;
	int counter = 0;

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "factorialBackendRouter");

	public FactorialFrontend(int upToN, boolean repeat) {
		this.upToN = upToN;
		this.repeat = repeat;
	}

	@Override
	public void preStart() {
		sendJobs();
		getContext().setReceiveTimeout(Duration.create(5, TimeUnit.SECONDS));
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(FactorialResult.class, result -> {
//			logger.info("result factorial= " + result.factorial + ", n= " + result.n + ", upToN=" + upToN);
//			System.out.print(".");
//				logger.info("---------<<<<<<<<<<<<<<-------->{}! = {}", result.n, result.factorial);
				if (repeat){
					sendJobs();
//					logger.info("Simply blocked here");
				}else
					getContext().stop(self());
		}).match(String.class, message -> {
			log.info("I Got it back! "+message);
			sendJobs();
		}).match(ReceiveTimeout.class, message -> {
			log.info("Timeout");
			sendJobs();
		}).build();
	}

	void sendJobs() {
//		log.info("Starting batch of factorials up to [{}]", upToN);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			backend.tell("Hi "+(counter++), self());

//			CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> new Integer(n));
//			pipe(result, getContext().dispatcher()).to(backend);
	}

}
