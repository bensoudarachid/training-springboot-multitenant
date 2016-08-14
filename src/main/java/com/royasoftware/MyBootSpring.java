package com.royasoftware;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

//import com.royasoftware.filter.SimpleFilter;

//import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

//EnableZuulProxy
@ComponentScan
@EnableAutoConfiguration
public class MyBootSpring {

	public static void main(String[] args) {
//		System.out.println("Hi");
		SpringApplication.run(MyBootSpring.class, args);
//		System.out.println("Hi tani");
		
	}

//	Bean
//	public SimpleFilter simpleFilter() {
//	  return new SimpleFilter();
//	}	
    /**
     * Create a CacheManager implementation class to be used by Spring where
     * <code>@Cacheable</code> annotations are applied.
     *
     * @return A CacheManager instance.
     */
//    @Bean
//    public CacheManager cacheManager() {
//
//        GuavaCacheManager cacheManager = new GuavaCacheManager("thisisit");
//
//        return cacheManager;
//    }

	
}