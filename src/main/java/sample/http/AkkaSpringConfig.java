package sample.http;

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

//import com.royasoftware.service.TrainingService;
//import com.royasoftware.service.TrainingServiceBean;


@Configuration
@EnableAutoConfiguration
//@EnableWebMvc
@SpringBootApplication
@PropertySource(ignoreResourceNotFound = false, value = "classpath:application.properties")
// SpringBootApplication replaces: @Configuration @ComponentScan
// @EnableAutoConfiguration
//@EnableScheduling
@ComponentScan(basePackages={"sample","com.royasoftware.service","com.royasoftware.repository","com.royasoftware.settings.security"})


//@Configuration
//@EnableAutoConfiguration
//@PropertySource(ignoreResourceNotFound = false, value = "classpath:application.properties")
//@ComponentScan(basePackages={"sample","com.royasoftware.service","com.royasoftware.repository","com.royasoftware.settings.security"})

public class AkkaSpringConfig {

//    @Bean(name="TrainingService")
//	public TrainingService getTrainingService(){
//		return new TrainingServiceBean();
//	}

}
