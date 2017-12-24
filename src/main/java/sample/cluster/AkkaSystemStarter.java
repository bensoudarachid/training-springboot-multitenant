package sample.cluster;

//import static sample.config.SpringExtension.SpringExtProvider;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CompletionStage;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.royasoftware.MyBootSpring;
import com.royasoftware.repository.TrainingRepository;
import com.royasoftware.service.TrainingService;
//import com.royasoftware.settings.configuration.AkkaSystemConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import sample.cluster.stats.StatsWorker;
import sample.config.AkkaSpringConfig;


public class AkkaSystemStarter  {
	private static Logger logger = LoggerFactory.getLogger(AkkaSystemStarter.class);
//	@Autowired
//	private ActorSystem actorSystem;
//	@Autowired
//	private SpringExtension springExtension;

	public static ActorSystem ACTOR_SYSTEM = null;
	static{
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=2552")
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
				.withFallback(ConfigFactory.load("stats1"));

		ACTOR_SYSTEM = ActorSystem.create("TrainingAkkaSystem", config);
	}

//	void init(){
//		logger.info("springExtension="+springExtension); 
//		logger.info("actorSystem="+actorSystem); 
//		ActorRef trainingServiceActor = actorSystem.actorOf(springExtension.props("TrainingServiceActor"));
//	}
//	private static final Logger logger = LogManager.getLogger(AkkaServerStarter.class.getName());
//	@Autowired
//	private static ApplicationContext applicationContext;

	// set up ActorSystem and other dependencies here

//	public AkkaSystemStarter(ActorSystem system, ActorRef userRegistryActor) {
//	}
	// #main-class

	public static void main(String[] args) {
		logger.info("Lets go!");
		Properties props = System.getProperties();
		props.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");		
//		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//        ctx.register(AkkaSpringConfig.class);
//        
//        ctx.refresh();		
//		logger.info("context=" + ctx);

		
		
		//		TrainingService trainingService = ctx.getBean(TrainingService.class); // ctx.getBean(TrainingService.class);
//		logger.info("trainingService.sayHello()="+trainingService.sayHello());
//		TrainingRepository trainingRepo = ctx.getBean(TrainingRepository.class); // ctx.getBean(TrainingService.class);

		// #server-bootstrapping
		// boot up server using the route as defined below
		// logger.info("applicationContext="+applicationContext);
//		if (ctx != null)
//			System.exit(0);
//		else if (ctx == null)
//			System.exit(1);

		
//		ActorSystem system = ActorSystem.create("helloAkkaHttpServer");
//	    SpringExtension springExtension = ctx.getBean(SpringExtension.class);
//	    logger.info("springExtension="+springExtension); 
//	    ActorSystem actorSystem = ctx.getBean(ActorSystem.class);
//	    logger.info("system="+actorSystem); 
//	    ActorRef trainingServiceActor = system.actorOf(springExtension.props("TrainingServiceActor"));
//		actorSystem.actorOf(springExtension.props("TrainingServiceActor"),"trainingServiceActor2");
		ActorRef workerRouter = ACTOR_SYSTEM.actorOf(Props.create(StatsWorker.class), "workerActor2");


//		ActorRef trainingServiceActor = system.actorOf(springExtension.props("TrainingServiceActor"),"factorialBackendRouter");
	    
//	    final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);

//		AkkaSystemStarter app = new AkkaSystemStarter(system, userRegistryActor);
//		ActorRef trainingServiceActor = actorSystem.actorOf(springExtension.props("TrainingServiceActor"));
		logger.info("Help Akka server up");
//		System.out.println("Server online at http://localhost:8070/\nPress RETURN to stop...");
		try {
			System.in.read(); // let it run until user presses return
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// #main-class
	/**
	 * Here you can define all the different routes you want to have served by
	 * this web server Note that routes might be defined in separated classes
	 * like the current case
	 */
}
// #main-class
