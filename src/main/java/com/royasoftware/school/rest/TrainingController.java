package com.royasoftware.school.rest;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
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
import org.w3c.dom.Document;

import com.royasoftware.school.TenantContext;
import com.royasoftware.school.cluster.SpringExtension;
import com.royasoftware.school.exception.ValidationException;
import com.royasoftware.school.model.Training;
import com.royasoftware.school.service.TrainingServFrEndActor;
import com.royasoftware.school.service.TrainingService;
import com.royasoftware.school.service.TrainingServFrEndActor.Trainings;
import com.royasoftware.school.settings.security.CustomUserDetails;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

@RestController
@RequestMapping("/api/**")
public class TrainingController extends BaseController {
//	@Autowired
//	private ActorSystem actorSystem;
	@Autowired
	private SpringExtension springExtension;

	@Autowired
	private TrainingService trainingService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/savetraining")
	public ResponseEntity<Training> saveTrainingObject(@RequestBody Training trainingParam) throws Exception {
		logger.info("Calling Post rest controller saveTrainingObject ");
		CustomUserDetails user = TenantContext.getCurrentUser();
		rdmTimeRdmSuccess();
		Training training = new Training();
		training.setTitle(trainingParam.getTitle());
		training.setShortDescription(trainingParam.getShortDescription());
		training.setLongDescription(trainingParam.getLongDescription());
		training = trainingService.saveTraining(training);
		return new ResponseEntity<Training>(training, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/deletetraining")
	public ResponseEntity<Boolean> deleteTraining(@RequestBody Training trainingParam) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		rdmTimeRdmSuccess();
		File dir = new File(TenantContext.getCurrentTenantStoragePath("trainings"));
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(trainingParam.getId() + ".");
			}
		});
		File file = null;
		for (int i = 0; files!=null&&i < files.length; i++) {
			files[i].delete();
		}
		trainingService.deleteTraining(trainingParam);

		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/updatetraining")
	public ResponseEntity<Training> updateTrainingObject(@RequestPart("trainingParam") Training trainingParam,
			@RequestPart(value = "uploadfile", required = false) MultipartFile file) throws Exception {
		logger.info("here we go updatetraining "+trainingParam);
		rdmTimeRdmSuccess();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Optional<Set<ConstraintViolation<Training>>> violations = Optional.of(validator.validate(trainingParam));
		if( violations.isPresent()&&!violations.get().isEmpty() ){
	        Map<String, String> validErrorMap = violations.get().stream().collect(
	                Collectors.toMap(v->v.getPropertyPath().toString(), ConstraintViolation::getMessage ) );
			logger.info("validErrorMap="+validErrorMap);
			throw new ValidationException(trainingParam,validErrorMap);
		}
		Training training = trainingService.updateTraining(trainingParam);
		if (file != null)
			fileUpload(training.getId(), file);
		
		return new ResponseEntity<Training>(training, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/save")
	public ResponseEntity<Training> saveTraining(@RequestPart("title") String title) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		logger.info("Calling Post rest controller save training ");
		rdmTimeRdmSuccess();
		Training training = new Training();
		training.setTitle(title);
		training.setShortDescription(title);
		training.setLongDescription(title);

		training = trainingService.saveTraining(training);
		return new ResponseEntity<Training>(training, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/{trainingId}/fileupload")
	public ResponseEntity<Object> fileUpload(@PathVariable Long trainingId,
			@RequestParam("uploadfile") MultipartFile file)
			throws Exception {
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		InputStream is = new BufferedInputStream(new ByteArrayInputStream(file.getBytes()));

		String extension = null;
		if (file.getOriginalFilename().endsWith(".svg")) {
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
			try {
				Document doc = f.createDocument(null, is);
				extension = "svg";
			} catch (Exception e) {
				throw new Exception("Invalid SVG file");
			}
		} else {
			ImageInputStream iis = ImageIO.createImageInputStream(is);
			Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
			if (!imageReaders.hasNext()) {
				throw new Exception("Uploaded File is not an image!");
			}
			ImageReader reader = imageReaders.next();
			extension = reader.getFormatName();
			if (extension == null)
				throw new Exception("Uploaded File is not an image!");
		}
		String uploadPath = TenantContext.getCurrentTenantStoragePath("trainings");
		File uploadFilePath = new File(uploadPath);
		File uploadFile = new File(uploadPath + trainingId + "." + extension);

		if( !uploadFilePath.mkdirs() )
			logger.info("Directory already there : "+uploadPath);
		else{
			logger.info("Good. Created directories : "+uploadFilePath.getAbsolutePath());
		}
		// Delete all files beginning with thw id number
		File uploadDirectory = new File(uploadPath);
		File[] files = uploadDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(trainingId + ".");
			}
		});
		if (files != null && files.length > 0)
			for (int i = 0; i < files.length; i++)
				files[i].delete();

		// Now store the new file
		FileOutputStream fos = new FileOutputStream(uploadFile);
		fos.write(file.getBytes());
		fos.close();

		rdmTimeRdmSuccess();
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/trainings/{_param}")
	public ResponseEntity<Object> getTrainingsPost(@PathVariable String _param) throws Exception {
		return getTrainingsGet(_param);
	}

	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/trainings/{_param}")
	public ResponseEntity<Object> getTrainingsGet(@PathVariable String _param) throws Exception {
		logger.info("--- trainingList controller get list ");
		
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		rdmTimeRdmSuccess();
//		ActorSelection trainingServFrEndActor = actorSystem.actorSelection("/user/trainingServFrEndActor");
//		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
//		Future<Object> future = Patterns.ask(trainingServFrEndActor, new TrainingServFrEndActor.GetTrainings(), timeout);
//		ArrayList<Training> trainingList = (ArrayList<Training>) Await.result(future, timeout.duration());

		
//		future = Patterns.ask(trainingServFrEndActor, new TrainingServFrEndActor.Message("Controller. Hi"), timeout);
//		String mesgBack= (String) Await.result(future, timeout.duration());
//		logger.info("Controller got mesg Back = "+mesgBack); 

//		future = Patterns.ask(trainingServFrEndActor, new TrainingServFrEndActor.Message("Controller. Hi"), timeout);
//		Trainings trainings2= (Trainings) Await.result(future, timeout.duration());
//		Vector<Training> trainingList2 = trainings.getTrainings();
//		logger.info("mesgBack="+trainingList2.firstElement()); 

//		future = Patterns.ask(trainingServFrEndActor, new TrainingServFrEndActor.Message("Controller. Hi"), timeout);
//		Training training= (Training) Await.result(future, timeout.duration());
//		logger.info("mesgBack="+training); 
		
//		ActorSelection workerRouter = MyBootSpring.ACTOR_SYSTEM.actorSelection("/user/workerRouter");
//		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
//		Future<Object> future = Patterns.ask(workerRouter, new StatsMessages.SayHi(), timeout);
//		Await.result(future, timeout.duration());

		
		Collection<Training> trainingList = trainingService.findAll();
		logger.info("--- trainingList size =" + trainingList.size());
		return new ResponseEntity<Object>(trainingList, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/item/{_param}")
	public ResponseEntity<Training> getTraining(@PathVariable Long _param) throws Exception {
		rdmTimeRdmSuccess();
		Training training = trainingService.findById(_param);
		logger.info("training title ="+training.getTitle()); 
		logger.info("training duration ="+training.getDuration()); 
		return new ResponseEntity<Training>(training, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.IMAGE_PNG_VALUE }, value = "/training/img/{_param}")
	public ResponseEntity<Object> getTrainingImage(@PathVariable Long _param, @RequestParam("width") Integer width,
			@RequestParam("height") Integer height) throws Exception {
		width = width > 200 ? 200 : width;
		height = height > 200 ? 200 : height;

		rdmTimeRdmSuccess();

		File dir = new File(TenantContext.getCurrentTenantStoragePath("trainings"));

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
			Image scaledImg = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (file.getName().endsWith(".svg")) {
				ImageIO.write(generatePngFromSvg(file, width, height), "png", baos);
			} else {
				BufferedImage originalImage = ImageIO.read(file);
				scaledImg = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = thumbnail.createGraphics();

				g.setColor(Color.decode("#f4faff"));
				g.fillRect(0, 0, width, height);

				Point2D center = new Point2D.Float(width/2, width/2);
				float[] dist = { 0.0f, 0.5f };
				Color[] colors = { Color.decode("#ffffff"), Color.decode("#d7e7f4") };
				RadialGradientPaint p = new RadialGradientPaint(center, width, dist, colors);

				g.setPaint(p);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.fillOval(0, 0, width, height);
				g.drawImage(scaledImg, 0, 0, null);
				g.dispose();
				g.setComposite(AlphaComposite.Src);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				ImageIO.write(thumbnail, "jpg", baos);
			}
			baos.flush();
			ret = baos.toByteArray();
			baos.close();

		} else {
			ret = null;
		}
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/training/imgtest/{_param}")
	public void getTrainingImg(@PathVariable String _param, HttpServletResponse response) throws Exception {
		rdmTimeRdmSuccess();

		File dir = new File(TenantContext.getCurrentTenantStoragePath("trainings"));

		// list the files using a anonymous FileFilter
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(_param + ".");
				// return file.getName().equals(_param);
			}
		});
		File file = null;
		if (files == null || files.length == 0)
			return;
		file = files[0];
		for (int i = 1; i < files.length; i++)
			files[i].delete();

		byte[] ret;

		if (file.exists()) {
			logger.info("i, Calling new Get rest controller get training image " + file.getName());

			InputStream myStream = new FileInputStream(file);

			// Set the content type and attachment header.
			response.addHeader("Content-disposition", "attachment;filename=myfilename.txt");
			response.setContentType("svg-xml");

			// Copy the stream to the response's output stream.
			copy(myStream, response.getOutputStream());
			response.flushBuffer();
		}
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