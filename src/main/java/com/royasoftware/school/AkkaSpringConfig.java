package com.royasoftware.school;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.royasoftware.school.repository.TrainingRepository;
import com.royasoftware.school.service.TrainingService;

import akka.actor.ActorSystem;

//import com.royasoftware.service.TrainingService;
//import com.royasoftware.service.TrainingServiceBean;


@Configuration
//@EnableAutoConfiguration
//@SpringBootApplication
@PropertySource(ignoreResourceNotFound = false, value = {"classpath:application.properties","classpath:helpakkaserver.properties"})
// SpringBootApplication replaces: @Configuration @ComponentScan
@EnableAutoConfiguration
@EnableAspectJAutoProxy

//@EnableScheduling
@ComponentScan(basePackages={"com.royasoftware.school.cluster","com.royasoftware.school.service","com.royasoftware.school.repository","com.royasoftware.settings.security"})
//@ComponentScan(basePackages={"sample","com.royasoftware"})
//@ComponentScan(basePackages={"sample.cluster"})

@EnableTransactionManagement 
@EnableJpaRepositories(basePackages = "com.royasoftware.school.repository")
@EntityScan("com.royasoftware.school.model")

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
