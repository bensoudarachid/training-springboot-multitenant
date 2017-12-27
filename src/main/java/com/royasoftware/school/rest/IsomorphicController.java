package com.royasoftware.school.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IsomorphicController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@RequestMapping(value="/hi", method=RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> render(Model model) {
		logger.info("isom render"); 
		model.addAttribute("title", "Layout example");
//		model.addAttribute("comments", this.commentRepository.findAll());
		return new ResponseEntity<String>("hi", HttpStatus.OK);
	}

//    @GetMapping("/{path:(?!.*.js|.*.css|.*.jpg).*$}")
//    public String index(Model model, HttpServletRequest request) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//
//        Map<String, Object> req = new HashMap<>();
//        String root = request.getServletPath().equals("/index.html") ? "/" : request.getServletPath();
//        if(request.getQueryString() != null)
//            req.put("location", String.format("%s?%s", root, request.getQueryString()));
//        else
//            req.put("location", root);
//        model.addAttribute("req", mapper.writeValueAsString(req));
//
//        Map<String, Object> initialState = new HashMap<>();
////        initialState.put("items", itemRepository.findAll());
//        model.addAttribute("initialState", mapper.writeValueAsString(initialState));
//
//        return "index";    
//    }
}
