package com.royasoftware.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.User;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.royasoftware.settings.security.CustomUserDetails;



@RestController
@RequestMapping("/api/**")
public class HelloWorldController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@RequestMapping(method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE},value="/{_param}")
	public String getProcess(@PathVariable String _param, @AuthenticationPrincipal CustomUserDetails activeUser){
		logger.info("Calling Post Spring controller param "+_param );
		
//		return "forward:/test2?param1=foo&param2=bar";
		return getTodos( _param, activeUser);
//		return "Hello mama: "+_param;
	}
	@RequestMapping(method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE},value="/todos/{_param}")
	public String getTodos(@PathVariable String _param, @AuthenticationPrincipal CustomUserDetails activeUser){
		
		logger.info("User connected as bound parameter: name = "+activeUser.getUsername() + ", id = "+activeUser.getId());
		logger.info("Get Todos");
//		return "forward:/test2?param1=foo&param2=bar";
//		return "{todos: [{task: 'make it now 7bayby',isCompleted: false,id: 24},{task: 'ya do it 7bayby',isCompleted: false,id: 25}]}"; 

		return "{"+
        "\"todos\": ["+ 
          "{"+
            "\"task\": \"make it now 7bayby\","+
            "\"isCompleted\": false,"+
            "\"id\": 4"+	
          "},"+
          "{"+
            "\"task\": \"ya do it 7bayby\","+
            "\"isCompleted\": true,"+
            "\"id\": 5"+
          "}"+
        "]"+
      "}";
	}
}