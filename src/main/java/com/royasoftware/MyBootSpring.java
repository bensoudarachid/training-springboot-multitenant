package com.royasoftware;

import static akka.pattern.Patterns.ask;
//import static sample.config.SpringExtension.SpringExtProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;

import com.royasoftware.script.ScriptHelper;
import com.royasoftware.service.TrainingServiceActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import akka.stream.ActorMaterializer;
import akka.util.Timeout;
import sample.cluster.simple.CountingActor.Get;
import sample.cluster.stats.StatsSampleClientMain;
import sample.cluster.stats.StatsWorker;
import sample.cluster.AkkaSystemStarter;
import sample.cluster.SpringExtension;
import sample.cluster.simple.CountingActor.Count;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

//import com.royasoftware.filter.SimpleFilter;

//import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

//EnableZuulProxy

@EnableWebMvc
@SpringBootApplication
// SpringBootApplication replaces: @Configuration @ComponentScan
// @EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = { "sample", "com.royasoftware" })
// @ComponentScan(basePackages={"com.royasoftware"})
@PropertySource(ignoreResourceNotFound = false, value = { "classpath:application.properties",
		"classpath:mainakkaserver.properties" })

public class MyBootSpring extends SpringBootServletInitializer implements SchedulingConfigurer {
	private static Logger logger = LoggerFactory.getLogger(MyBootSpring.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MyBootSpring.class);
	}
	public static ActorSystem ACTOR_SYSTEM = null;
	static{
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=2551")
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
				.withFallback(ConfigFactory.load("stats1"));

		ACTOR_SYSTEM = ActorSystem.create("TrainingAkkaSystem", config);
	}
	
	
	
	static private void writeDemoDataToUserStorage(String tenant) {
		try {

			logger.info("writeDemoDataToUserStorage " + tenant);
			String fileName = "images/" + tenant + ".svg";
			ClassPathResource cpr = new ClassPathResource(fileName);

			String logoFilePath = TenantContext.getTenantStoragePath(tenant) + "profile";
			File logoFileFolder = new File(logoFilePath);
			logoFileFolder.mkdirs();
			System.out.println("writeDemoDataToUserStorage mkdirs");

			String logoFileStr = TenantContext.getTenantStoragePath(tenant) + "profile/logo.svg";
			System.out.println("logoFileStr=" + logoFileStr);
			File logoFile = new File(logoFileStr);
			FileOutputStream fos = null;
			if (!logoFile.exists()) {
				fos = new FileOutputStream(logoFile);
				fos.write(FileCopyUtils.copyToByteArray(cpr.getInputStream()));
				fos.close();
			}

			fileName = "data/" + tenant + ".properties";
			cpr = new ClassPathResource(fileName);

			String dataFileStr = TenantContext.getTenantStoragePath(tenant) + "profile/data.properties";
			File dataFile = new File(dataFileStr);

			if (!dataFile.exists()) {
				fos = new FileOutputStream(dataFile);
				fos.write(FileCopyUtils.copyToByteArray(cpr.getInputStream()));
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static private void writeDemoDataToUserStorageold(String tenant) {
		try {
			System.out.println("writeDemoDataToUserStorage " + tenant);
			String fileName = "images/" + tenant + ".svg";
			ClassLoader classLoader = MyBootSpring.class.getClassLoader();
			File file = new File(classLoader.getResource(fileName).getFile());
			System.out.println("logoFileStr file=" + file);
			System.out.println("logoFileStr file path=" + file.getPath());

			String logoFilePath = TenantContext.getTenantStoragePath(tenant) + "profile";
			File logoFileFolder = new File(logoFilePath);
			logoFileFolder.mkdirs();
			System.out.println("writeDemoDataToUserStorage mkdirs");

			String logoFileStr = TenantContext.getTenantStoragePath(tenant) + "profile/logo.svg";
			System.out.println("logoFileStr=" + logoFileStr);
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
		Properties props = System.getProperties();
		// System property is needed for the async disruptor logger.
		props.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

		writeDemoDataToUserStorage("demo1");
		writeDemoDataToUserStorage("demo2");
		writeDemoDataToUserStorage("abbaslearn");

		ApplicationContext ctx = SpringApplication.run(MyBootSpring.class, args);
		// ScriptHelper.run(ScriptHelper.RUN_WEB_APP);
		System.out.println("Hi tani");

//		ActorSystem actorSystem = ctx.getBean(ActorSystem.class);
		// SpringExtension springExtension = ctx.getBean(SpringExtension.class);

		// final ActorMaterializer materializer =
		// ActorMaterializer.create(system);
		// ActorRef userRegistryActor =
		// system.actorOf(SpringExtProvider.get(system).props("UserRegistryActor"),
		// "userRegistry");
		// logger.info("Main Akka server up");
		// AkkaSystemStarter.main(new String[0]);

		// ActorRef trainingServiceActor =
		// actorSystem.actorOf(springExtension.props("TrainingServiceActor"),"trainingServiceActor1");
		// actorSystem.actorOf(springExtension.props("TrainingServiceActor"),"trainingServiceRouter");
		// actorSystem.actorOf(springExtension.props("TrainingServiceActor"),"trainingServiceRouter");

//		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=2552")
//				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
//				.withFallback(ConfigFactory.load("stats1"));
//
//	    ActorSystem system = ActorSystem.create("ClusterSystem", config);

//		actorSystem.actorOf(Props.create(StatsWorker.class), "statsWorker1");
		// ActorRef workerRouter = system.actorOf(
		// FromConfig.getInstance().props(Props.create(StatsWorker.class)),
		// "workerRouter");
		ActorRef workerRouter = ACTOR_SYSTEM.actorOf(Props.create(StatsWorker.class), "workerRouter");
		ACTOR_SYSTEM.actorOf(Props.create(StatsWorker.class), "workerActor1");

		// FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
		// Future<Object> result = ask(counter, new Get(),
		// Timeout.durationToTimeout(duration));
		// try {
		// System.out.println("Got back " + Await.result(result, duration));
		// } catch (Exception e) {
		// System.err.println("Failed getting result: " + e.getMessage());
		// } finally {
		// system.terminate();
		//
		// }

	}

	// Bean
	// public SimpleFilter simpleFilter() {
	// return new SimpleFilter();
	// }
	/**
	 * Create a CacheManager implementation class to be used by Spring where
	 * <code>@Cacheable</code> annotations are applied.
	 *
	 * @return A CacheManager instance.
	 */
	// @Bean
	// public CacheManager cacheManager() {
	//
	// GuavaCacheManager cacheManager = new GuavaCacheManager("thisisit");
	//
	// return cacheManager;
	// }

	// @Bean
	// public EmbeddedServletContainerFactory servletContainer() {
	//
	// TomcatEmbeddedServletContainerFactory tomcat = new
	// TomcatEmbeddedServletContainerFactory();
	//
	// Connector ajpConnector = new Connector("AJP/1.3");
	// ajpConnector.setProtocol("AJP/1.3");
	// ajpConnector.setPort(9090);
	// ajpConnector.setSecure(false);
	// ajpConnector.setAllowTrace(false);
	// ajpConnector.setScheme("http");
	// tomcat.addAdditionalTomcatConnectors(ajpConnector);
	//
	// return tomcat;
	// }

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

	// @Bean
	// public ViewResolver reactViewResolver() {
	// ScriptTemplateViewResolver viewResolver = new
	// ScriptTemplateViewResolver();
	// viewResolver.setPrefix("static2/");
	// viewResolver.setSuffix(".ejs");
	// return viewResolver;
	// }
	@Bean
	public ViewResolver viewResolver() {
		return new ScriptTemplateViewResolver("/static2/", ".html");
	}

	@Bean
	public ScriptTemplateConfigurer reactConfigurer() {
		ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
		configurer.setEngineName("nashorn");
		configurer.setScripts("static2/polyfill.js", "static2/lib/js/ejs.min.js", "static2/lib/js/react.js",
				"static2/render.js",
				// "D:/RP/Tests/ReactToDoExp2/node_modules/react-dom/dist/react-dom.js",
				// "/META-INF/resources/webjars/react/0.13.1/JSXTransformer.js",
				"static/vendor.bundle.js", "static/1.bundle.js", "static2/server.js"
		// "static/output/comment.js",
		// "static/output/comment-form.js",
		// "static/output/comment-list.js"
		);
		configurer.setRenderFunction("render");
		configurer.setSharedEngine(false);
		return configurer;
	}
}
