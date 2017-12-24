package sample.cluster.factorial;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.AbstractActor;
import sample.cluster.stats.StatsWorker;
import sample.cluster.stats.StatsMessages.StatsJob;
import sample.cluster.transformation.TransformationMessages.TransformationResult;

import static akka.pattern.PatternsCS.pipe;

public class FactorialBackend extends AbstractActor {
	static Logger logger = LogManager.getLogger(StatsWorker.class.getName());

	public FactorialBackend() {
		logger.info("###########################################FactorialBackend constructor " + new Random().nextInt());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(Integer.class, n -> {
//			logger.info("process n="+n); 
			System.out.print(".");
			CompletableFuture<FactorialResult> result = CompletableFuture.supplyAsync(() -> factorial(n))
					.thenApply((factorial) -> new FactorialResult(n, factorial));
//			logger.info("backend "+this.getSelf().path()+" pipe for n = "+ n);
			pipe(result, getContext().dispatcher()).to(sender());

//			BigInteger facResult = factorial(n);
//			FactorialResult result = new FactorialResult(n, facResult);
//			sender().tell(result, self());
			
		}).match(String.class, msg -> {
			logger.info("Hallo "+msg);
			sender().tell("Hallo "+msg, self());
		}).build();
	}

	BigInteger factorial(int n) {
		BigInteger acc = BigInteger.ONE;
		for (int i = 1; i <= n; ++i) {
			acc = acc.multiply(BigInteger.valueOf(i));
		}
		return acc;
	}
}
