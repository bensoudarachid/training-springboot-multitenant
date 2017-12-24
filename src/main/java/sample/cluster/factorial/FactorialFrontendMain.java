package sample.cluster.factorial;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import com.royasoftware.MyBootSpring;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.pattern.Patterns;
import akka.routing.FromConfig;
import akka.util.Timeout;
import sample.cluster.stats.StatsMessages;
import sample.cluster.stats.StatsSampleClient;

public class FactorialFrontendMain {

	public static void main(String[] args) {
		final int upToN = 2;

		final Config config = ConfigFactory.parseString("akka.cluster.roles = [frontend]")
				.withFallback(ConfigFactory.load("factorial"));

		final ActorSystem system = ActorSystem.create("ClusterSystem", config);
		system.log().info("Factorials will start when 2 backend members in the cluster.");
		
		
		
//		ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "factorialBackendRouter");
//		ActorRef workerRouter = getContext().actorOf(FromConfig.getInstance().props(Props.create(StatsWorker.class)),
//				"workerRouter");
//		system.actorOf(Props.create(StatsSampleClient.class, "/user/statsService"),"client");		

		try {
//			ActorRef backend = system.actorOf(Props.create(FactorialBackend.class, upToN, true), "factorialBackendRouter");
			ActorRef backend = system.actorOf(FromConfig.getInstance().props(), "factorialBackendRouter");
			Timeout timeout = new Timeout(Duration.create(5, "seconds"));
			Future<Object> future = Patterns.ask(backend, "Hi ", timeout);
			Await.result(future, timeout.duration());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		
		
		
//		Cluster.get(system).registerOnMemberUp(new Runnable() {
//			@Override
//			public void run() {
//				system.actorOf(Props.create(FactorialFrontend.class, upToN, true), "factorialFrontend");
//			}
//		});
//
//		Cluster.get(system).registerOnMemberRemoved(new Runnable() {
//			@Override
//			public void run() {
//				// exit JVM when ActorSystem has been terminated
//				final Runnable exit = new Runnable() {
//					@Override
//					public void run() {
//						System.exit(0);
//					}
//				};
//				system.registerOnTermination(exit);
//
//				// shut down ActorSystem
//				system.terminate();
//
//				// In case ActorSystem shutdown takes longer than 10 seconds,
//				// exit the JVM forcefully anyway.
//				// We must spawn a separate thread to not block current thread,
//				// since that would have blocked the shutdown of the
//				// ActorSystem.
//				new Thread() {
//					@Override
//					public void run() {
//						try {
//							Await.ready(system.whenTerminated(), Duration.create(10, TimeUnit.SECONDS));
//						} catch (Exception e) {
//							System.exit(-1);
//						}
//
//					}
//				}.start();
//			}
//		});
	}

}
