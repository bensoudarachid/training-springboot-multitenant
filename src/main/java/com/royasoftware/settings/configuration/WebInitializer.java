package com.royasoftware.settings.configuration;

import com.royasoftware.MyBootSpring;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class WebInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MyBootSpring.class);
    }
}

//Spring 1.5.1
//package com.royasoftware.settings.configuration;
//
//import com.royasoftware.MyBootSpring;
//import org.springframework.boot.builder.SpringApplicationBuilder;
////import org.springframework.boot.context.web.SpringBootServletInitializer;
//import org.springframework.boot.web.support.SpringBootServletInitializer;
//
//
//public class WebInitializer extends SpringBootServletInitializer {
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(MyBootSpring.class);
//    }
//}
