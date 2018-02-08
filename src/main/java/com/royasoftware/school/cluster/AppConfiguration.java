package com.royasoftware.school.cluster;

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
 * Akka app config. 
 */

//@Configuration
class AppConfiguration {
	private static Logger logger = LoggerFactory.getLogger(AppConfiguration.class);
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private SpringExtension springExtension;

//	@Bean
	public ActorSystem actorSystem(@Value("${akka.server.port}") String port) {
		System.out.println("Create ClusterSystem on port: "+port); 
		ActorSystem system = null;
		String role = "backend"; 
		if( port.equals("2551") )
			role = "frontend";
		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = ["+role+"]"))
				.withFallback(ConfigFactory.load("clustercfg"));
			system = ActorSystem.create("ClusterSystem", config);
		springExtension.initialize(applicationContext);
		return system;
	}
}
