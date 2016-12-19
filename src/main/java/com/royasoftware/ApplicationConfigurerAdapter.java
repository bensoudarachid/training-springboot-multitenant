package com.royasoftware;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.royasoftware.rest.AppControllerInterceptor;

@Configuration
public class ApplicationConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {

        registry.addInterceptor(new AppControllerInterceptor())
                .addPathPatterns("/**");
    }

}