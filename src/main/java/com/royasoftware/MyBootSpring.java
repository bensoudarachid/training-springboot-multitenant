package com.royasoftware;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;

import com.royasoftware.script.ScriptHelper;

//import com.royasoftware.filter.SimpleFilter;

//import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

//EnableZuulProxy

@EnableWebMvc
@SpringBootApplication 
//SpringBootApplication replaces: @Configuration @ComponentScan @EnableAutoConfiguration
@EnableScheduling
public class MyBootSpring extends SpringBootServletInitializer implements SchedulingConfigurer{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MyBootSpring.class);
	}

	public static void main(String[] args) {

//		System.out.println("this.getClass().getResource(log4j.xml)="+MyBootSpring.class.getResourceAsStream("/db/datasource/tenants/school1.properties")); 
//		System.out.println("this.getClass().getResource(log4j.xml)="+MyBootSpring.class.getResource("/log4j.xml")); 
//		DOMConfigurator.configure("/log4j.xml");
//		DOMConfigurator.configure("file:/D:/RP/Tests/SpringBoot_Part_1/target/classes/log4j.xml");
		// System.out.println("Hi");

		// Flyway flyway = new Flyway();
		// flyway.setDataSource("jdbc:mysql://localhost:3306/todospring1",
		// "root", "1qay2wsx");
		// flyway.setLocations("db.migration");
		// flyway.migrate();
		// flyway = new Flyway();
		// flyway.setDataSource("jdbc:mysql://localhost:3306/todospring2",
		// "root", "1qay2wsx");
		// flyway.setLocations("db.migration");
		// flyway.migrate();
		SpringApplication.run(MyBootSpring.class, args);
		System.out.println("Hi tani");
		ScriptHelper.run(ScriptHelper.RUN_WEB_APP);

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

//	@Bean
//	public EmbeddedServletContainerFactory servletContainer() {
//
//	    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//
//	    Connector ajpConnector = new Connector("AJP/1.3");
//	    ajpConnector.setProtocol("AJP/1.3");
//	    ajpConnector.setPort(9090);
//	    ajpConnector.setSecure(false);
//	    ajpConnector.setAllowTrace(false);
//	    ajpConnector.setScheme("http");
//	    tomcat.addAdditionalTomcatConnectors(ajpConnector);
//
//	    return tomcat;
//	}
	
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }
 
    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
        if (rootAppContext != null) {
            servletContext.addListener(new CleanupListener());
        	logger.info("you could add your servlet listeners here!");
        }
        else {
            this.logger.debug("No ContextLoaderListener registered, as "
                    + "createRootApplicationContext() did not "
                    + "return an application context");
        }
       
    }
//    @Bean
//    public ViewResolver reactViewResolver() {
//        ScriptTemplateViewResolver viewResolver = new ScriptTemplateViewResolver();
//        viewResolver.setPrefix("static2/");
//        viewResolver.setSuffix(".ejs");
//        return viewResolver;
//    }
    @Bean
    public ViewResolver viewResolver() {
        return new ScriptTemplateViewResolver("/static2/", ".html");
    }    
    @Bean
    public ScriptTemplateConfigurer reactConfigurer() {
        ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
        configurer.setEngineName("nashorn");
        configurer.setScripts(
        		"static2/polyfill.js",
                "static2/lib/js/ejs.min.js",
                "static2/lib/js/react.js",
                "static2/render.js",
//                "D:/RP/Tests/ReactToDoExp2/node_modules/react-dom/dist/react-dom.js",
//                "/META-INF/resources/webjars/react/0.13.1/JSXTransformer.js",
                "static/vendor.bundle.js",
                "static/1.bundle.js",
                "static2/server.js"
//                "static/output/comment.js",
//                "static/output/comment-form.js",
//                "static/output/comment-list.js"
                );
        configurer.setRenderFunction("render");
        configurer.setSharedEngine(false);
        return configurer;
    }
}
