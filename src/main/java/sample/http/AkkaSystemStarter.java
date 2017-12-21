package sample.http;

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

import com.royasoftware.MyBootSpring;
import com.royasoftware.repository.TrainingRepository;
import com.royasoftware.service.TrainingService;
import com.royasoftware.settings.configuration.AkkaSystemConfig;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import static sample.SpringExtension.SpringExtProvider;

//#main-class
public class AkkaSystemStarter  {
	private static Logger logger = LoggerFactory.getLogger(AkkaSystemStarter.class);
//	private static final Logger logger = LogManager.getLogger(AkkaServerStarter.class.getName());
//	@Autowired
//	private static ApplicationContext applicationContext;

	// set up ActorSystem and other dependencies here

//	public AkkaSystemStarter(ActorSystem system, ActorRef userRegistryActor) {
//	}
	// #main-class

	public static void main(String[] args) throws Exception {
		logger.info("Lets go!");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AkkaSpringConfig.class);
        
        ctx.refresh();		
		logger.info("context=" + ctx);
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
	    ActorSystem system = ctx.getBean(ActorSystem.class);

		final ActorMaterializer materializer = ActorMaterializer.create(system);

//		ActorRef userRegistryActor = system.actorOf(UserRegistryActor.props(), "userRegistryActor");
		ActorRef userRegistryActor = system.actorOf(
			      SpringExtProvider.get(system).props("UserRegistryActor"), "userRegistry");

//		AkkaSystemStarter app = new AkkaSystemStarter(system, userRegistryActor);

		logger.info("Akka server up");
//		System.out.println("Server online at http://localhost:8070/\nPress RETURN to stop...");
		System.in.read(); // let it run until user presses return

	}

	// #main-class
	/**
	 * Here you can define all the different routes you want to have served by
	 * this web server Note that routes might be defined in separated classes
	 * like the current case
	 */
}
// #main-class
