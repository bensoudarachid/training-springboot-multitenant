package com.royasoftware.school.rest;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.royasoftware.school.TenantContext;
import com.royasoftware.school.model.Todo;
import com.royasoftware.school.service.TodoService;
import com.royasoftware.school.settings.security.CustomUserDetails;

@RestController
public class HealthController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int IMG_WIDTH = 100;
	private static final int IMG_HEIGHT = 100;

	@Autowired
	private TodoService todoService;

	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/ishealthy")
	public ResponseEntity<Todo> getHealth() throws Exception {
		Todo todo = new Todo();
		todo.setTask("Wow");
		todo.setCompleted(false);
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
	}

}