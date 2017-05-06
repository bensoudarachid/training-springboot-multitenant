package com.royasoftware.rest;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.ext.awt.image.codec.util.MemoryCacheSeekableStream;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
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
import org.w3c.dom.svg.SVGDocument;

import com.royasoftware.TenantContext;
import com.royasoftware.model.Training;
import com.royasoftware.service.TrainingService;
import com.royasoftware.settings.security.CustomUserDetails;
import com.royasoftware.utils.SVGValidator;

@RestController
// RequestMapping("/reactor/api/**")
@RequestMapping("/api/**")
public class TrainingController extends BaseController {
	@Autowired
	private TrainingService trainingService;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/savetraining")
	public ResponseEntity<Training> saveTrainingObject(@RequestBody Training trainingParam) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		logger.info("Calling Post rest controller save training as object param " + trainingParam.getTitle() + ", id = "
				+ trainingParam.getId());
		logger.info(
				"Calling Post rest controller save training as object param " + trainingParam.getShortDescription());
		rdmTimeRdmSuccess();
		Training training = new Training();
		training.setTitle(trainingParam.getTitle());
		training.setShortDescription(trainingParam.getShortDescription());
		training.setLongDescription(trainingParam.getLongDescription());
		training = trainingService.saveTraining(training);
		// return "forward:/test2?param1=foo&param2=bar";
		return new ResponseEntity<Training>(training, HttpStatus.OK);
		// return "Hello mama: "+_param;
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/deletetraining")
	public ResponseEntity<Boolean> deleteTraining(@RequestBody Training trainingParam) throws Exception {
		CustomUserDetails user = TenantContext.getCurrentUser();
		logger.info("Calling Post rest controller delete training as object param " + trainingParam.getTitle()
				+ ", id = " + trainingParam.getId());
		rdmTimeRdmSuccess();
		// try {
		// Random rand = new Random();
		// int random = rand.nextInt(100);
		// Thread.sleep(50 * random);
		// // if( random > 50 )
		// // return new ResponseEntity("Shit",
		// // HttpStatus.INTERNAL_SERVER_ERROR);
		// } catch (InterruptedException e) {
		// // Training Auto-generated catch block
		// e.printStackTrace();
		// }
		File dir = new File(TenantContext.getCurrentTenantStoragePath("trainings"));
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(trainingParam.getId() + ".");
			}
		});
		File file = null;
		logger.info("DELETE file Go:"+files);
		for (int i = 0; files!=null&&i < files.length; i++) {
			logger.info("DELETE file"+files[i]);
			files[i].delete();
		}
		trainingService.deleteTraining(trainingParam);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/updatetraining")
	public ResponseEntity<Training> updateTrainingObject(@RequestPart("trainingParam") Training trainingParam,
			@RequestPart(value = "uploadfile", required = false) MultipartFile file) throws Exception {
		// String subdomain = getSubdomain();
		logger.info("Calling Post rest controller upload training. title" + trainingParam);
		if (file != null)
			logger.info("File loaded as bound parameter: orig name = " + file.getOriginalFilename() + ", "
					+ file.getName());
		// TenantContext.setCurrentTenant(subdomain);
		// logger.info("Calling Post rest controller upload training. host = " +
		// request.getHeader("host"));
		// logger.info("Calling Post rest controller update training as object
		// param " + trainingParam.getTitle()
		// + ", id = " + trainingParam.getId());
		// logger.info(
		// "Calling Post rest controller update training as object param version
		// = " + trainingParam.getVersion());
		rdmTimeRdmSuccess();
		// Training training = new Training();
		// training.setId(trainingParam.getId());
		// training.setTask(trainingParam.getTask());
		// training.setCompleted(trainingParam.isCompleted());
		// training = trainingService.updateTraining(training, user.getId());
		// CustomUserDetails activeUser = TenantContext.getCurrentUser();
		// Training training = trainingService.updateTraining(trainingParam);
		Training training = trainingService.updateTraining(trainingParam);
		if (file != null)
			fileUpload(training.getId(), file);
		return new ResponseEntity<Training>(training, HttpStatus.OK);
	}

	// @AuthenticationPrincipal CustomUserDetails user
	// @RequestMapping(method = RequestMethod.POST, produces = {
	// MediaType.APPLICATION_JSON_VALUE }, value =
	// "/training/updatetrainingSolution1")
	// public ResponseEntity<Training> updateTrainingObjectSolution1(
	// @RequestPart("title") String title,
	// @RequestPart("secondaryTitle") String secondaryTitle,
	// @RequestPart("shortDescription") String shortDescription,
	// @RequestPart("longDescription") String longDescription,
	// @RequestParam(value="uploadfile", required=false) MultipartFile file
	// ) throws Exception {
	// // String subdomain = getSubdomain();
	// logger.info("Calling Post rest controller upload training. title"+title);
	// if( file!= null)
	// logger.info("File loaded as bound parameter: orig name = " +
	// file.getOriginalFilename() + ", " + file.getName());
	// // TenantContext.setCurrentTenant(subdomain);
	// // logger.info("Calling Post rest controller upload training. host = " +
	// // request.getHeader("host"));
	//// logger.info("Calling Post rest controller update training as object
	// param " + trainingParam.getTitle()
	//// + ", id = " + trainingParam.getId());
	//// logger.info(
	//// "Calling Post rest controller update training as object param version =
	// " + trainingParam.getVersion());
	// rdmTimeRdmSuccess();
	// // Training training = new Training();
	// // training.setId(trainingParam.getId());
	// // training.setTask(trainingParam.getTask());
	// // training.setCompleted(trainingParam.isCompleted());
	// // training = trainingService.updateTraining(training, user.getId());
	//// CustomUserDetails activeUser = TenantContext.getCurrentUser();
	//// Training training = trainingService.updateTraining(trainingParam);
	// return null;
	// }

	// @RequestMapping(method = RequestMethod.POST, produces = {
	// MediaType.APPLICATION_JSON_VALUE }, value = "/training/updatetraining")
	// public ResponseEntity<Training> updateTrainingObject(@RequestBody
	// Training trainingParam) throws Exception {
	// // String subdomain = getSubdomain();
	// // logger.info("Calling Post rest controller upload training. subdomain
	// // = "
	// // + subdomain);
	// // TenantContext.setCurrentTenant(subdomain);
	// // logger.info("Calling Post rest controller upload training. host = " +
	// // request.getHeader("host"));
	// logger.info("Calling Post rest controller update training as object param
	// " + trainingParam.getTitle()
	// + ", id = " + trainingParam.getId());
	// logger.info(
	// "Calling Post rest controller update training as object param version = "
	// + trainingParam.getVersion());
	// rdmTimeRdmSuccess();
	// // Training training = new Training();
	// // training.setId(trainingParam.getId());
	// // training.setTask(trainingParam.getTask());
	// // training.setCompleted(trainingParam.isCompleted());
	// // training = trainingService.updateTraining(training, user.getId());
	//// CustomUserDetails activeUser = TenantContext.getCurrentUser();
	// Training training = trainingService.updateTraining(trainingParam);
	// return new ResponseEntity<Training>(training, HttpStatus.OK);
	// }

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
		// return "forward:/test2?param1=foo&param2=bar";
		return new ResponseEntity<Training>(training, HttpStatus.OK);
		// return "Hello mama: "+_param;
	}

	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/{trainingId}/fileupload")
	public ResponseEntity<Object> fileUpload(@PathVariable Long trainingId,
			@RequestParam("uploadfile") MultipartFile file)
			// HttpServletRequest request,
			// HttpServletResponse response)
			throws Exception {
		logger.info("Calling Upload training param is" + trainingId);
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		logger.info("User connected as bound parameter: name = " + activeUser.getUsername() + ", id = "
				+ activeUser.getId() + ", role = " + activeUser.getAuthorities().toString());
		logger.info(
				"File loaded as bound parameter: orig name = " + file.getOriginalFilename() + ", " + file.getName());
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
			logger.info("SVG loaded successfully");
			// return ResponseEntity.status(HttpStatus.OK).body(null);
		} else {
			ImageInputStream iis = ImageIO.createImageInputStream(is);
			Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
			if (!imageReaders.hasNext()) {
				throw new Exception("Uploaded File is not an image!");
				// return new ResponseEntity<>("Uploaded File is not an image!",
				// HttpStatus.BAD_REQUEST);
				// return new ResponseEntity<>("Uploaded File is not an image!",
				// HttpStatus.BAD_REQUEST);
			}
			ImageReader reader = imageReaders.next();
			extension = reader.getFormatName();
			System.out.printf("++++++++++++++++++formatName: %s%n", extension);
			if (extension == null)
				throw new Exception("Uploaded File is not an image!");
		}
		// return new ResponseEntity<>("Uploaded File is not an image!",
		// HttpStatus.BAD_REQUEST);
		String uploadPath = TenantContext.getCurrentTenantStoragePath("trainings");
		File uploadFilePath = new File(uploadPath);
		File uploadFile = new File(uploadPath + trainingId + "." + extension);

		// logger.info("file upload here: Size=" + file.getBytes()+",
		// path="+abbas.getAbsolutePath());
		uploadFilePath.mkdirs();

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
		logger.info("storing in " + uploadFile.getPath());
		FileOutputStream fos = new FileOutputStream(uploadFile);
		fos.write(file.getBytes());
		fos.close();

		rdmTimeRdmSuccess();
		// Training training = new Training();
		// training.setId(trainingParam.getId());
		// training.setTask(trainingParam.getTask());
		// training.setCompleted(trainingParam.isCompleted());
		// training = trainingService.updateTraining(training, user.getId());
		// Training training = trainingService.updateTraining(trainingParam,
		// user.getId());
		// return new ResponseEntity(null,HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	// @AuthenticationPrincipal CustomUserDetails activeUser
	// @ PreAuthorize("permitAll()")
	@RequestMapping(method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/trainings/{_param}")
	public ResponseEntity<Object> getTrainingsPost(@PathVariable String _param) throws Exception {
//		logger.info("Hanalik! Calling Post rest controller get trainings " + _param);
		// return "forward:/test2?param1=foo&param2=bar";
		return getTrainingsGet(_param);
		// return "Hello mama: "+_param;
	}

	// @ PreAuthorize("isAuthenticated()")
	
	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/trainings/{_param}")
	public ResponseEntity<Object> getTrainingsGet(@PathVariable String _param) throws Exception {
		logger.info("Calling Post rest controller get trainings " + _param);
		// TenantContext.setCurrentTenant(subdomain);
		CustomUserDetails activeUser = TenantContext.getCurrentUser();
		// logger.info("User connected as bound parameter: name = " +
		// activeUser.getUsername() + ", id = "
		// + activeUser.getId() + ", role = " +
		// activeUser.getAuthorities().toString());

		// logger.info("User connected as bound parameter: name = " +
		// activeUser.getUsername() + ", id = "
		// + activeUser.getId() + ", role = " +
		// activeUser.getAuthorities().toString());
		logger.info("Calling Get rest controller get trainings " + _param);
		rdmTimeRdmSuccess();
		// logger.info("Get Trainings for user "+activeUser.getId()+". Size:
		// "+trainingService.findByUserId(activeUser.getId()).size());

		// return "forward:/test2?param1=foo&param2=bar";
		// return "{trainings: [{task: 'make it now 7bayby',isCompleted:
		// false,id:
		// 24},{task: 'ya do it 7bayby',isCompleted: false,id: 25}]}";
		Collection<Training> trainingList = trainingService.findAll();
		logger.info("Thread! trainingList size =" + trainingList.size());
		return new ResponseEntity<Object>(trainingList, HttpStatus.OK);
		// return "{"+
		// "\"trainings\": ["+
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

	// @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE }, value = "/training/item/{_param}")
	public ResponseEntity<Training> getTraining(@PathVariable Long _param) throws Exception {
		// CustomUserDetails activeUser = TenantContext.getCurrentUser();
		logger.info("Calling Get rest controller get training id = " + _param);
		rdmTimeRdmSuccess();
		Training training = trainingService.findById(_param);
		// logger.info("training found =" + training);
		return new ResponseEntity<Training>(training, HttpStatus.OK);
	}

	// @PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.IMAGE_PNG_VALUE }, value = "/training/img/{_param}")
	public ResponseEntity<Object> getTrainingImage(@PathVariable Long _param, @RequestParam("width") Integer width,
			@RequestParam("height") Integer height) throws Exception {
		// CustomUserDetails activeUser = TenantContext.getCurrentUser();
		// if( activeUser== null )
		// return new ResponseEntity<Object>(null,
		// HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
		// else
		// logger.info("User connected as bound parameter: name = " +
		// activeUser.getUsername() + ", id = "
		// + activeUser.getId() + ", role = " +
		// activeUser.getAuthorities().toString());
		// logger.info("Calling Get rest controller get training image " +
		// _param);
		width = width > 200 ? 200 : width;
		height = height > 200 ? 200 : height;

//		 logger.info("Get Training Image "+_param+", width "+width+", height"+height);
		rdmTimeRdmSuccess();
		// TenantContext.getCurrentUser().getId();
		// TenantContext.getCurrentTenantStoragePath();

		File dir = new File(TenantContext.getCurrentTenantStoragePath("trainings"));

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

		// File file = new
		// File(TenantContext.getCurrentUserStoragePath()+_param+".png");
		byte[] ret;

		if (file.exists()) {
			// Open the file, then read it in.
			// BufferedImage originalImage = null;
			Image scaledImg = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (file.getName().endsWith(".svg")) {
				ImageIO.write(generatePngFromSvg(file, width, height), "png", baos);
				// FileInputStream ios = new FileInputStream(file);
				// int read = 0;
				// byte[] buffer = new byte[4096];
				// while ((read = ios.read(buffer)) != -1) {
				// baos.write(buffer, 0, read);
				// }
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
				// g.setPaint(Color.decode("#d7e7f4"));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.fillOval(0, 0, width, height);

				// g.drawImage(scaledImg,0,0,new Color(197, 211, 226),null);

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

			// ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			// InputStream inStream = new FileInputStream(file);
			// copy(inStream, outStream);
			// inStream.close();
			// outStream.close();
			// ret = outStream.toByteArray();
		} else {
			ret = null;
		}
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	// @RequestMapping(value = "/training/uploadfile", method =
	// RequestMethod.POST)
	// public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile
	// file) {
	// try {
	// logger.info("file upload here: " + file.getOriginalFilename());
	// } catch (Exception e) {
	// return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	// }
	// return new ResponseEntity<>(HttpStatus.OK);
	// }
	private BufferedImage generatePngFromSvg(File file, Integer width, Integer height) throws Exception {
		FileInputStream fr = new FileInputStream(file);
		TranscoderInput input_svg_image = new TranscoderInput(fr);
		// Step-2: Define OutputStream to PNG Image and attach to
		// TranscoderOutput
		// OutputStream png_ostream = new FileOutputStream("chessboard.png");
		ByteArrayOutputStream png_ostream = new ByteArrayOutputStream();
		TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
		// Step-3: Create PNGTranscoder and define hints if required
		PNGTranscoder my_converter = new PNGTranscoder();
		// Step-4: Convert and Write output
		my_converter.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
		my_converter.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(height));
		my_converter.transcode(input_svg_image, output_png_image);
		// Step 5- close / flush Output Stream
		png_ostream.flush();
		byte[] ret = png_ostream.toByteArray();
		png_ostream.close();

		// ByteArrayInputStream is = new ByteArrayInputStream(ret);

		return ImageIO.read(new ByteArrayInputStream(ret));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/training/imgtest/{_param}")
	public void getTrainingImg(@PathVariable String _param, HttpServletResponse response) throws Exception {
		// CustomUserDetails activeUser = TenantContext.getCurrentUser();
		// if( activeUser== null )
		// return new ResponseEntity<Object>(null,
		// HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
		// else
		// logger.info("User connected as bound parameter: name = " +
		// activeUser.getUsername() + ", id = "
		// + activeUser.getId() + ", role = " +
		// activeUser.getAuthorities().toString());
		rdmTimeRdmSuccess();
		// TenantContext.getCurrentUser().getId();
		// TenantContext.getCurrentTenantStoragePath();
		logger.info("Call get image. Param=" + _param);

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

		// File file = new
		// File(TenantContext.getCurrentUserStoragePath()+_param+".png");
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
			if (logger.isDebugEnabled()) {
				logger.debug("Copying content...");
			}
		} catch (IOException ioe) {
			logger.info(ioe.toString());
			throw ioe;
		}
	}

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
				// Training Auto-generated catch block
				e.printStackTrace();
			}
		// return true;
	}

}