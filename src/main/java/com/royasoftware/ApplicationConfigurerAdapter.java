package com.royasoftware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.royasoftware.rest.AppControllerInterceptor;

@Configuration
public class ApplicationConfigurerAdapter extends WebMvcConfigurerAdapter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
//    	logger.info("Register AppControllerInterceptor");
        registry.addInterceptor(new AppControllerInterceptor());//.addPathPatterns("/**");
    }

}