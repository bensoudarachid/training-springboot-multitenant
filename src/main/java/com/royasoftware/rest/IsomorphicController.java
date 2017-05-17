package com.royasoftware.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class IsomorphicController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	@RequestMapping(value="/hello", method=RequestMethod.GET)
//	String render(Model model) {
//		logger.info("isom render"); 
//		model.addAttribute("title", "Layout example");
////		model.addAttribute("comments", this.commentRepository.findAll());
//		return "index";
//	}

    @GetMapping("/{path:(?!.*.js|.*.css|.*.jpg).*$}")
    public String index(Model model, HttpServletRequest request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> req = new HashMap<>();
        String root = request.getServletPath().equals("/index.html") ? "/" : request.getServletPath();
        if(request.getQueryString() != null)
            req.put("location", String.format("%s?%s", root, request.getQueryString()));
        else
            req.put("location", root);
        model.addAttribute("req", mapper.writeValueAsString(req));

        Map<String, Object> initialState = new HashMap<>();
//        initialState.put("items", itemRepository.findAll());
        model.addAttribute("initialState", mapper.writeValueAsString(initialState));
        
        return "index";    
    }
}
