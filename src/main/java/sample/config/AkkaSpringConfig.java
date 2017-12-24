package sample.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.royasoftware.repository.TrainingRepository;
import com.royasoftware.service.TrainingService;

import akka.actor.ActorSystem;
import sample.cluster.AkkaSystemStarter;
import sample.cluster.SpringExtension;

//import com.royasoftware.service.TrainingService;
//import com.royasoftware.service.TrainingServiceBean;


//@Configuration
//@EnableAutoConfiguration
@SpringBootApplication
@PropertySource(ignoreResourceNotFound = false, value = {"classpath:application.properties","classpath:helpakkaserver.properties"})
// SpringBootApplication replaces: @Configuration @ComponentScan
// @EnableAutoConfiguration
//@EnableScheduling
@ComponentScan(basePackages={"sample","com.royasoftware.service","com.royasoftware.repository","com.royasoftware.settings.security"})
//@ComponentScan(basePackages={"sample.cluster"})


//@Configuration
//@EnableAutoConfiguration
//@PropertySource(ignoreResourceNotFound = false, value = "classpath:application.properties")
//@ComponentScan(basePackages={"sample","com.royasoftware.service","com.royasoftware.repository","com.royasoftware.settings.security"})

public class AkkaSpringConfig {
	private static Logger logger = LoggerFactory.getLogger(AkkaSpringConfig.class);
//	@Autowired
//	private ActorSystem actorSystem;
//	@Autowired
//	private SpringExtension springExtension;

	AkkaSpringConfig(){
//		logger.info("AkkaSpringConfig, Hi");
//		logger.info("springExtension="+springExtension); 
//		logger.info("actorSystem="+actorSystem); 
	}
//    @Bean(name="TrainingService")
//	public TrainingService getTrainingService(){
//		return new TrainingServiceBean();
//	}

}
