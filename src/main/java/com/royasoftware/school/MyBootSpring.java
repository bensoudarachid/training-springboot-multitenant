package com.royasoftware.school;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;

import com.royasoftware.school.script.ScriptHelper;

@EnableWebMvc
@SpringBootApplication
// SpringBootApplication replaces: @Configuration @ComponentScan
@EnableAspectJAutoProxy
@EnableRabbit
@EnableScheduling
@EnableCaching
// @ComponentScan(basePackages = { "com.royasoftware" })
@PropertySource(ignoreResourceNotFound = false, value = { "classpath:application.properties",
		"classpath:mainakkaserver.properties" })

public class MyBootSpring extends SpringBootServletInitializer implements SchedulingConfigurer { // ,RabbitListenerConfigurer
	private static Logger logger = LoggerFactory.getLogger(MyBootSpring.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MyBootSpring.class);
	}

	static private void writeDemoDataToUserStorage(String tenant) {
		try {
			String fileName = "images/" + tenant + ".svg";
			ClassPathResource cpr = new ClassPathResource(fileName);

			String logoFilePath = TenantContext.getTenantStoragePath(tenant) + "profile";
			File logoFileFolder = new File(logoFilePath);
			logoFileFolder.mkdirs();

			String logoFileStr = TenantContext.getTenantStoragePath(tenant) + "profile/logo.svg";
			File logoFile = new File(logoFileStr);
			FileOutputStream fos = null;
			if (logoFile.exists()) {
				logoFile.delete();
			}
			// if (!logoFile.exists()) {
			fos = new FileOutputStream(logoFile);
			fos.write(FileCopyUtils.copyToByteArray(cpr.getInputStream()));
			fos.close();
			// }

			fileName = "data/" + tenant + ".properties";
			cpr = new ClassPathResource(fileName);

			String dataFileStr = TenantContext.getTenantStoragePath(tenant) + "profile/data.properties";
			File dataFile = new File(dataFileStr);
			if (dataFile.exists()) {
				dataFile.delete();
			}
			// if (!dataFile.exists()) {
			fos = new FileOutputStream(dataFile);
			fos.write(FileCopyUtils.copyToByteArray(cpr.getInputStream()));
			fos.close();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static private void writeDemoDataToUserStorageold(String tenant) {
		try {
			String fileName = "images/" + tenant + ".svg";
			ClassLoader classLoader = MyBootSpring.class.getClassLoader();
			File file = new File(classLoader.getResource(fileName).getFile());

			String logoFilePath = TenantContext.getTenantStoragePath(tenant) + "profile";
			File logoFileFolder = new File(logoFilePath);
			logoFileFolder.mkdirs();

			String logoFileStr = TenantContext.getTenantStoragePath(tenant) + "profile/logo.svg";
			File logoFile = new File(logoFileStr);
			FileOutputStream fos = null;
			if (!logoFile.exists()) {
				fos = new FileOutputStream(logoFile);
				fos.write(Files.readAllBytes(file.toPath()));
				fos.close();
			}

			fileName = "data/" + tenant + ".properties";
			classLoader = MyBootSpring.class.getClassLoader();
			file = new File(classLoader.getResource(fileName).getFile());

			String dataFileStr = TenantContext.getTenantStoragePath(tenant) + "profile/data.properties";
			File dataFile = new File(dataFileStr);

			if (!dataFile.exists()) {
				fos = new FileOutputStream(dataFile);
				fos.write(Files.readAllBytes(file.toPath()));
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// Thread t = new Thread(() -> {
		// AkkaSystemStarter.main(new String[0]);
		// });
		// t.start();
		Properties props = System.getProperties();
		// System property is needed for the async disruptor logger.
		props.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

		writeDemoDataToUserStorage("demo1");
		writeDemoDataToUserStorage("demo2");
		writeDemoDataToUserStorage("abbaslearn");

		ApplicationContext ctx = SpringApplication.run(MyBootSpring.class, args);
		// ActorSystem actorSystem = ctx.getBean(ActorSystem.class);
		// SpringExtension springExtension = ctx.getBean(SpringExtension.class);
		// actorSystem.actorOf(springExtension.props("TrainingServFrEndActor"),
		// "trainingServFrEndActor");

//		ScriptHelper.run(ScriptHelper.RUN_TEMP2);

		logger.info("Spring Boot Server started");

	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(10);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
		if (rootAppContext != null) {
			servletContext.addListener(new CleanupListener());
			logger.info("you could add your servlet listeners here!");
		} else {
			this.logger.debug("No ContextLoaderListener registered, as " + "createRootApplicationContext() did not "
					+ "return an application context");
		}
	}

	@Bean
	public ViewResolver viewResolver() {
		return new ScriptTemplateViewResolver("/static2/", ".html");
	}

}
