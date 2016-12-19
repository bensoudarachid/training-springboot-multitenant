package com.royasoftware.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.royasoftware.TenantContext;
import com.royasoftware.model.Todo;
import com.royasoftware.service.TodoService;
import com.royasoftware.settings.security.CustomUserDetails;

@RestController
// RequestMapping("/reactor/api/**")
@RequestMapping("/api/**")
public class TodosController extends BaseController {
	@Autowired
	private TodoService todoService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/savetodo")
	public ResponseEntity<Todo> saveTodoObject(@RequestBody Todo todoParam) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		logger.info("Calling Post rest controller save todo as object param " + todoParam.getTask() + ", id = "
				+ todoParam.getId());
		logger.info("Calling Post rest controller save todo as object param " + todoParam.isCompleted());
		rdmTimeRdmSuccess();
		Todo todo = new Todo();
		todo.setTask(todoParam.getTask());
		todo.setCompleted(false);
		todo = todoService.saveTodo(todo, user.getId());
		// return "forward:/test2?param1=foo&param2=bar";
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
		// return "Hello mama: "+_param;
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/deletetodo")
	public ResponseEntity<Boolean> deleteTodo(@RequestBody Todo todoParam) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		logger.info("Calling Post rest controller delete todo as object param " + todoParam.getTask() + ", id = "
				+ todoParam.getId());
		rdmTimeRdmSuccess();
		// try {
		// Random rand = new Random();
		// int random = rand.nextInt(100);
		// Thread.sleep(50 * random);
		// // if( random > 50 )
		// // return new ResponseEntity("Shit",
		// // HttpStatus.INTERNAL_SERVER_ERROR);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		todoService.deleteTodo(todoParam, user.getId());
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	// @AuthenticationPrincipal CustomUserDetails user
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/updatetodo")
	public ResponseEntity<Todo> updateTodoObject(@RequestBody Todo todoParam) throws Exception {
		// String subdomain = getSubdomain();
		// logger.info("Calling Post rest controller upload todo. subdomain = "
		// + subdomain);
		// TenantContext.setCurrentTenant(subdomain);
		// logger.info("Calling Post rest controller upload todo. host = " +
		// request.getHeader("host"));
		logger.info("Calling Post rest controller update todo as object param " + todoParam.getTask() + ", id = "
				+ todoParam.getId());
		logger.info("Calling Post rest controller update todo as object param version = " + todoParam.getVersion());
		rdmTimeRdmSuccess();
		// Todo todo = new Todo();
		// todo.setId(todoParam.getId());
		// todo.setTask(todoParam.getTask());
		// todo.setCompleted(todoParam.isCompleted());
		// todo = todoService.updateTodo(todo, user.getId());
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		Todo todo = todoService.updateTodo(todoParam, activeUser.getId());
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE }, value = "/todo/save")
	public ResponseEntity<Todo> saveTodo(@RequestPart("task") String task) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		logger.info("Calling Post rest controller save todo ");
		rdmTimeRdmSuccess();
		Todo todo = new Todo();
		todo.setTask(task);
		todo = todoService.saveTodo(todo, user.getId());
		// return "forward:/test2?param1=foo&param2=bar";
		return new ResponseEntity<Todo>(todo, HttpStatus.OK);
		// return "Hello mama: "+_param;
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todo/{todoId}/fileupload")
	public ResponseEntity<Object> fileUpload(@PathVariable String todoId,
			@RequestParam("uploadfile") MultipartFile file)
			// HttpServletRequest request,
			// HttpServletResponse response)
			throws Exception {
		logger.info("Calling Upload todo param is" + todoId);
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		logger.info("User connected as bound parameter: name = " + activeUser.getUsername() + ", id = "
				+ activeUser.getId() + ", role = " + activeUser.getAuthorities().toString());
		logger.info(
				"File loaded as bound parameter: orig name = " + file.getOriginalFilename() + ", " + file.getName());
//		if (file.getOriginalFilename().contains(".")) {
//			extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//			logger.info("File extension = " + extension);
//		} else {
//			throw new Exception("Unknown Extension");
//		}
		try {
			InputStream is = new BufferedInputStream(new ByteArrayInputStream(file.getBytes()));
			ImageInputStream iis = ImageIO.createImageInputStream(is); 
			Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
			if (!imageReaders.hasNext()) {
			    System.out.printf("++++++++++++++++++Abort the shit now: ");
//				throw new Exception("Uploaded File is not an image!");
				return new ResponseEntity<>("Uploaded File is not an image!", HttpStatus.BAD_REQUEST);
			}			
			ImageReader reader = imageReaders.next();
			String extension = reader.getFormatName();
		    System.out.printf("++++++++++++++++++formatName: %s%n", extension);
		    if(extension == null )
		    	return new ResponseEntity<>("Uploaded File is not an image!", HttpStatus.BAD_REQUEST);
		    String uploadPath = TenantContext.getCurrentTenantStoragePath() + "user/" + activeUser.getId() + "/todos/";
			File uploadFilePath = new File(uploadPath);
			File uploadFile = new File(uploadPath + todoId +"."+ extension);
			logger.info("storing in " + uploadFile.getPath());
			// logger.info("file upload here: Size=" + file.getBytes()+",
			// path="+abbas.getAbsolutePath());
			uploadFilePath.mkdirs();

			
			FileOutputStream fos = new FileOutputStream(uploadFile);

			fos.write(file.getBytes());
			fos.close();
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		rdmTimeRdmSuccess();
		// Todo todo = new Todo();
		// todo.setId(todoParam.getId());
		// todo.setTask(todoParam.getTask());
		// todo.setCompleted(todoParam.isCompleted());
		// todo = todoService.updateTodo(todo, user.getId());
		// Todo todo = todoService.updateTodo(todoParam, user.getId());
//		return new ResponseEntity(null,HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	// @AuthenticationPrincipal CustomUserDetails activeUser
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todos/{_param}")
	public ResponseEntity<Object> getTodosPost(@PathVariable String _param) throws Exception {
		logger.info("Calling Post rest controller get todos " + _param);
		// return "forward:/test2?param1=foo&param2=bar";
		return getTodosGet(_param);
		// return "Hello mama: "+_param;
	}

	// @AuthenticationPrincipal CustomUserDetails activeUser
	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/todos/{_param}")
	public ResponseEntity<Object> getTodosGet(@PathVariable String _param) throws Exception {
		// String subdomain = getSubdomain();
		// logger.info("Calling Post rest controller upload todo. subdomain = "
		// + subdomain);
		// TenantContext.setCurrentTenant(subdomain);
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		logger.info("User connected as bound parameter: name = " + activeUser.getUsername() + ", id = "
				+ activeUser.getId() + ", role = " + activeUser.getAuthorities().toString());
		logger.info("Calling Get rest controller get todos " + _param);
		rdmTimeRdmSuccess();
		// logger.info("Get Todos for user "+activeUser.getId()+". Size:
		// "+todoService.findByUserId(activeUser.getId()).size());

		// return "forward:/test2?param1=foo&param2=bar";
		// return "{todos: [{task: 'make it now 7bayby',isCompleted: false,id:
		// 24},{task: 'ya do it 7bayby',isCompleted: false,id: 25}]}";
		return new ResponseEntity<Object>(todoService.findByUserId(activeUser.getId()), HttpStatus.OK);
		// return "{"+
		// "\"todos\": ["+
		// "{"+
		// "\"task\": \"make it now 7bayby\","+
		// "\"isCompleted\": false,"+
		// "\"id\": 4"+
		// "},"+
		// "{"+
		// "\"task\": \"ya do it 7bayby\","+
		// "\"isCompleted\": true,"+
		// "\"id\": 5"+
		// "}"+
		// "]"+
		// "}";
	}

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.IMAGE_PNG_VALUE }, value = "/todo/img/{_param}")
	public ResponseEntity<Object> getTodoImage(@PathVariable String _param) throws Exception {
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		logger.info("User connected as bound parameter: name = " + activeUser.getUsername() + ", id = "
				+ activeUser.getId() + ", role = " + activeUser.getAuthorities().toString());
		logger.info("Calling Get rest controller get todos " + _param);
		rdmTimeRdmSuccess();

		File file = new File("useruploadstorage/abbas.png");
		byte[] ret;

		if (file.exists()) {
			// Open the file, then read it in.
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			InputStream inStream = new FileInputStream(file);
			copy(inStream, outStream);
			inStream.close();
			outStream.close();
			ret = outStream.toByteArray();
		} else {
			ret = null;
		}
		return new ResponseEntity<Object>(ret, HttpStatus.NOT_FOUND);
	}

//	@RequestMapping(value = "/todo/uploadfile", method = RequestMethod.POST)
//	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
//		try {
//			logger.info("file upload here: " + file.getOriginalFilename());
//		} catch (Exception e) {
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}

	private void rdmTimeRdmSuccess() throws Exception {
		boolean RDM_TIME = true;
		boolean RDM_SUCCESS = true;

		RDM_TIME = false;
		RDM_SUCCESS = false;

		if (RDM_TIME)
			try {
				Random rand = new Random();
				int random = rand.nextInt(100);
				Thread.sleep(50 * random);
				if (RDM_SUCCESS && random > 50)
					throw new Exception("Random Rejection"); //
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// return true;
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
			if (logger.isDebugEnabled()) {
				logger.debug("Copying image...");
			}
		} catch (IOException ioe) {
			logger.info(ioe.toString());
			throw ioe;
		}
	}

}