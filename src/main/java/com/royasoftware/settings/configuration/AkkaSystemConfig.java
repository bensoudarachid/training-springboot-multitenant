package com.royasoftware.settings.configuration;

import static sample.SpringExtension.SpringExtProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.royasoftware.repository.TrainingRepository;
import com.royasoftware.service.TrainingService;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import sample.cluster.simple.CountingActor.Count;

//import com.royasoftware.service.TrainingService;
//import com.royasoftware.service.TrainingServiceBean;

@Configuration
// @AnnotationDrivenConfig
//@EnableAutoConfiguration
//@PropertySource(ignoreResourceNotFound = false, value = "classpath:application.properties")
//@ComponentScan(basePackages = { "sample", "com.royasoftware.service", "com.royasoftware.repository",
//		"com.royasoftware.settings.security" })
// @ComponentScan(basePackageClasses = {TrainingService.class,
// TrainingRepository.class})

public class AkkaSystemConfig {
	  @Autowired
	  private ApplicationContext applicationContext;
	  @Bean
	  public ActorSystem actorSystem() {
	    ActorSystem system = ActorSystem.create("AkkaJavaSpring");
	    // initialize the application context in the Akka Spring Extension
	    SpringExtProvider.get(system).initialize(applicationContext);
	    return system;
	  }

	// @Bean(name="TrainingService")
	// public TrainingService getTrainingService(){
	// return new TrainingServiceBean();
	// }
//	public static void main(String[] args) {
//		ActorSystem system = applicationContext.getBean(ActorSystem.class);
//		ActorRef counter = system.actorOf(SpringExtProvider.get(system).props("CountingActor"), "counter");
//
//		// tell it to count three times
//		counter.tell(new Count(), null);
//	}
}
