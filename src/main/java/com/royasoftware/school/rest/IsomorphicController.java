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
		return new ResponseEntity<String>("hi", HttpStatus.OK);
	}

}
