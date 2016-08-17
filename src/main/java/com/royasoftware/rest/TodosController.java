package com.royasoftware.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.royasoftware.model.Todo;
import com.royasoftware.service.AccountService;
import com.royasoftware.service.TodoService;
import com.royasoftware.settings.security.CustomUserDetails;
import java.util.Random;


@RestController
@RequestMapping("/api/**")
public class TodosController extends BaseController {
	@Autowired
	private TodoService todoService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@RequestMapping(method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE},value="/todo/save")
	public ResponseEntity<Todo> saveTodo(@RequestParam("task") String task, @AuthenticationPrincipal CustomUserDetails user){
		logger.info("Calling Post rest controller save todo " );
		try {
			Random rand = new Random();
			int random = rand.nextInt(100);
			Thread.sleep(50*random);
			if( random > 30 )
				return new ResponseEntity("Shit", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Todo todo = new Todo();
		todo.setTask(task);
		todo = todoService.saveTodo(todo, user.getId());
//		return "forward:/test2?param1=foo&param2=bar";
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
//		return "Hello mama: "+_param;
	}
	@RequestMapping(method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE},value="/todos/{_param}")
	public ResponseEntity<Object> getTodosGet(@PathVariable String _param, @AuthenticationPrincipal CustomUserDetails activeUser){
		logger.info("Calling Post rest controller get todos "+_param );
		
//		return "forward:/test2?param1=foo&param2=bar";
		return getTodosPost( _param, activeUser);
//		return "Hello mama: "+_param;
	}
	@RequestMapping(method=RequestMethod.GET,produces={MediaType.APPLICATION_JSON_VALUE},value="/todos/{_param}")
	public ResponseEntity<Object> getTodosPost(@PathVariable String _param, @AuthenticationPrincipal CustomUserDetails activeUser){
		
		logger.info("User connected as bound parameter: name = "+activeUser.getUsername() + ", id = "+activeUser.getId());
		logger.info("Calling Get rest controller get todos "+_param );
//		logger.info("Get Todos for user "+activeUser.getId()+". Size: "+todoService.findByUserId(activeUser.getId()).size());
		
		
//		return "forward:/test2?param1=foo&param2=bar";
//		return "{todos: [{task: 'make it now 7bayby',isCompleted: false,id: 24},{task: 'ya do it 7bayby',isCompleted: false,id: 25}]}"; 
		return new ResponseEntity<Object>(todoService.findByUserId(activeUser.getId()), HttpStatus.OK);
//		return "{"+
//        "\"todos\": ["+ 
//          "{"+
//            "\"task\": \"make it now 7bayby\","+
//            "\"isCompleted\": false,"+
//            "\"id\": 4"+	
//          "},"+
//          "{"+
//            "\"task\": \"ya do it 7bayby\","+
//            "\"isCompleted\": true,"+
//            "\"id\": 5"+
//          "}"+
//        "]"+
//      "}";
	}
}