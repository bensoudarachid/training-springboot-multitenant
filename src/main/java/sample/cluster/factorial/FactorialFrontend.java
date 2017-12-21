package sample.cluster.factorial;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import scala.concurrent.duration.Duration;

public class FactorialFrontend extends AbstractActor {
	private static Logger logger = LoggerFactory.getLogger(FactorialFrontend.class);
	final int upToN;
	final boolean repeat;

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "factorialBackendRouter");

	public FactorialFrontend(int upToN, boolean repeat) {
		this.upToN = upToN;
		this.repeat = repeat;
	}

	@Override
	public void preStart() {
		sendJobs();
		getContext().setReceiveTimeout(Duration.create(1, TimeUnit.SECONDS));
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(FactorialResult.class, result -> {
//			logger.info("result factorial= " + result.factorial + ", n= " + result.n + ", upToN=" + upToN);
//			System.out.print(".");
			if (result.n == upToN) {
//				logger.info("---------<<<<<<<<<<<<<<-------->{}! = {}", result.n, result.factorial);
				if (repeat){
					sendJobs();
//					logger.info("Simply blocked here");
				}else
					getContext().stop(self());
			}
		}).match(ReceiveTimeout.class, message -> {
			log.info("Timeout");
			sendJobs();
		}).build();
	}

	void sendJobs() {
//		log.info("Starting batch of factorials up to [{}]", upToN);
		for (int n = 1; n <= upToN; n++) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			backend.tell(n, self());

//			CompletableFuture<Integer> result = CompletableFuture.supplyAsync(() -> new Integer(n));
//			pipe(result, getContext().dispatcher()).to(backend);
		}
	}

}
