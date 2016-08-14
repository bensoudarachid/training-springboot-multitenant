package com.royasoftware.rest;

import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/**")
public class HelloWorldController {
	@RequestMapping(method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE},value="/{_param}")
	public String getProcess(@PathVariable String _param){
		System.out.println("Calling Post Spring controller param "+_param );
		
//		return "forward:/test2?param1=foo&param2=bar";
		return getTodos( _param);
//		return "Hello mama: "+_param;
	}
	@RequestMapping(method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE},value="/todos/{_param}")
	public String getTodos(@PathVariable String _param){
		
		System.out.println("Calling Get Spring controller param "+_param );
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