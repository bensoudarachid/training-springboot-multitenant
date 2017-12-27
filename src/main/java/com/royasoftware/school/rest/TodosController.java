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
@RequestMapping("/api/**")
public class TodosController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int IMG_WIDTH = 100;
	private static final int IMG_HEIGHT = 100;

	@Autowired
	private TodoService todoService;

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/savetodo")
	public ResponseEntity<Todo> saveTodoObject(@RequestBody Todo todoParam) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		rdmTimeRdmSuccess();
		Todo todo = new Todo();
		todo.setTask(todoParam.getTask());
		todo.setCompleted(false);
		todo = todoService.saveTodo(todo, user.getId());
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/deletetodo")
	public ResponseEntity<Boolean> deleteTodo(@RequestBody Todo todoParam) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		rdmTimeRdmSuccess();
		File dir = new File(TenantContext.getCurrentUserStoragePath("todos"));
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(todoParam.getId() + ".");
			}
		});
		File file = null;
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		todoService.deleteTodo(todoParam, user.getId());
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/updatetodo")
	public ResponseEntity<Todo> updateTodoObject(@RequestBody Todo todoParam) throws Exception {
		rdmTimeRdmSuccess();
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		Todo todo = todoService.updateTodo(todoParam, activeUser.getId());
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE }, value = "/todo/save")
	public ResponseEntity<Todo> saveTodo(@RequestPart("task") String task) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		rdmTimeRdmSuccess();
		Todo todo = new Todo();
		todo.setTask(task);
		todo = todoService.saveTodo(todo, user.getId());
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/{todoId}/fileupload")
	public ResponseEntity<Object> fileUpload(@PathVariable String todoId,
			@RequestParam("uploadfile") MultipartFile file)
			throws Exception {
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		InputStream is = new BufferedInputStream(new ByteArrayInputStream(file.getBytes()));
		ImageInputStream iis = ImageIO.createImageInputStream(is);
		Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
		if (!imageReaders.hasNext()) {
			throw new Exception("Uploaded File is not an image!");
		}
		ImageReader reader = imageReaders.next();
		String extension = reader.getFormatName();
		if (extension == null)
			return new ResponseEntity<>("Uploaded File is not an image!", HttpStatus.BAD_REQUEST);
		String uploadPath = TenantContext.getCurrentTenantStoragePath() + "user/" + activeUser.getId() + "/todos/";
		File uploadFilePath = new File(uploadPath);
		File uploadFile = new File(uploadPath + todoId + "." + extension);
		logger.info("storing in " + uploadFile.getPath());
		uploadFilePath.mkdirs();

		File uploadDirectory = new File(uploadPath);
		File[] files = uploadDirectory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(todoId + ".");
			}
		});
		if (files != null && files.length > 0)
			for (int i = 0; i < files.length; i++)
				files[i].delete();

		FileOutputStream fos = new FileOutputStream(uploadFile);

		fos.write(file.getBytes());
		fos.close();

		rdmTimeRdmSuccess();
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todos/{_param}")
	public ResponseEntity<Object> getTodosPost(@PathVariable String _param) throws Exception {
		return getTodosGet(_param);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todos/{_param}")
	public ResponseEntity<Object> getTodosGet(@PathVariable String _param) throws Exception {
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		rdmTimeRdmSuccess();
		return new ResponseEntity<Object>(todoService.findByUserId(activeUser.getId()), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.IMAGE_PNG_VALUE }, value = "/todo/img/{_param}")
	public ResponseEntity<Object> getTodoImage(@PathVariable String _param) throws Exception {
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		if (activeUser == null)
			return new ResponseEntity<Object>(null, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
		rdmTimeRdmSuccess();

		File dir = new File(TenantContext.getCurrentUserStoragePath("todos"));

		// list the files using a anonymous FileFilter
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(_param + ".");
			}
		});
		File file = null;
		if (files == null || files.length == 0)
			return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
		file = files[0];
		for (int i = 1; i < files.length; i++)
			files[i].delete();
		byte[] ret;

		if (file.exists()) {
			BufferedImage originalImage = ImageIO.read(file);
			Image scaledImg = originalImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			BufferedImage thumbnail = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = thumbnail.createGraphics();
			g.drawImage(scaledImg, 0, 0, null);
			g.dispose();
			g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(thumbnail, "jpg", baos);
			baos.flush();
			ret = baos.toByteArray();
		} else {
			ret = null;
		}
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	private void copy(InputStream source, OutputStream destination) throws IOException {
		try {
			// Transfer bytes from source to destination
			byte[] buf = new byte[1024];
			int len;
			while ((len = source.read(buf)) > 0) {
				destination.write(buf, 0, len);
			}
			source.close();
			destination.close();
		} catch (IOException ioe) {
			logger.info(ioe.toString());
			throw ioe;
		}
	}
}