package com.royasoftware;

import org.apache.log4j.xml.DOMConfigurator;
import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//import com.royasoftware.filter.SimpleFilter;

//import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

//EnableZuulProxy
@SpringBootApplication 
//SpringBootApplication replaces: @Configuration @ComponentScan @EnableAutoConfiguration
@EnableScheduling
public class MyBootSpring {

	public static void main(String[] args) {
		DOMConfigurator.configure("log4j.xml");
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
		// System.out.println("Hi tani");
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

}