package sample.cluster;

import akka.actor.ActorSystem;

//import static sample.config.SpringExtension.SpringExtProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * The application configuration.
 */
//@Configuration
class AppConfiguration {
//	private static Logger logger = LoggerFactory.getLogger(AppConfiguration.class);
	// the application context is needed to initialize the Akka Spring Extension
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private SpringExtension springExtension;

	@Bean
	public ActorSystem actorSystem(@Value("${akka.server.port}") String port) {
		System.out.println("----------------------------> Create mainActorSystem on port: "+port); 
		ActorSystem system = null;
//		if( port.equals("2551"))
		
	    final  Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=88" )
	    		.withFallback(ConfigFactory.load("stats1"));

		
//		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port)
//				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"));
//				.withFallback(ConfigFactory.load("factorial"));
//			system = ActorSystem.create("TrainingAkkaSystem", config);
//		else
//			System.out.println("----------------------------> Create mainActorSystem on port iwa?: "+port);
		// initialize the application context in the Akka Spring Extension
		springExtension.initialize(applicationContext);
		return system;
	}
//	@Bean
//	public Config akkaConfiguration(@Value("${akka.server.port}") String port){
//		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port)
//				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
//				.withFallback(ConfigFactory.load("factorial"));
//		return ConfigFactory.load(config);
//	}
}
