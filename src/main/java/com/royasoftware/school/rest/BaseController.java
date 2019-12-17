package com.royasoftware.school.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartException;
import org.springframework.web.context.request.WebRequest;

import com.google.common.util.concurrent.Uninterruptibles;
import com.royasoftware.school.exception.ValidationException;

/**
 * Base of all controllers
 */
public class BaseController {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private HttpServletRequest request;
    
	@ExceptionHandler(NoResultException.class)
	public @ResponseBody ErrorInfo handleNoResultException(NoResultException noResultException,
			HttpServletRequest request) {

		logger.info("handleNoResultException");

		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(getMessage(noResultException));
		response.setError(noResultException.getClass().getName());

		logger.info("handleNoResultException");
		return response;
	}

//	@ExceptionHandler(Exception.class)
//	public @ResponseBody ErrorInfo handleException(Exception exception, HttpServletRequest request) {
//
//		logger.error("Exception handler. ", getMessage(exception));
//		exception.printStackTrace();
//
//		ErrorInfo response = new ErrorInfo();
//		response.setUrl(request.getRequestURL().toString());
//		response.setErrorDescription(getMessage(exception));
//		response.setError(exception.getClass().getName());
//		return response;
//	}

	
	@ExceptionHandler({ Exception.class })
	public final ResponseEntity<ErrorInfo> handleException(Exception ex, HttpServletRequest request) {
		Throwable e = ex;
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(e.getMessage());
		response.setError(e.getClass().getName());

		if (e instanceof CompletionException)
			e = ex.getCause();
//		if (e instanceof ValidationException)
//			response.setValidation( ((ValidationException)e).getValidationErrorMap());
//			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
//		else
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler({ ValidationException.class })
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public final @ResponseBody ErrorInfo handleException(HttpServletRequest request, Exception ex) {
		Throwable e = ex;
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(e.getMessage());
		response.setError(e.getClass().getName());
		response.setValidation( ((ValidationException)e).getValidationErrorMap());
		return response;
	}
	  
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorInfo handleDataIntegrityException(HttpServletRequest request, Exception ex) {
		System.out.println("DataIntegrityViolationException handler. Here is the DataIntegrityViolationException handler." + ex.getClass().getName()
				+ ". message=" + getMessage(ex));
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription("Data Integrity Violation");
		return response;
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ErrorInfo handleAccessDeniedException(HttpServletRequest request, Exception ex) {
		System.out.println("AccessDeniedException handler. Here is the general exception handler." + ex.getClass().getName() + ". message="
				+ getMessage(ex));
		logger.info("AccessDeniedException. request.getRequestURL()="+request.getRequestURL()); 
		ex.printStackTrace();
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(getMessage(ex));
		response.setError(ex.getClass().getName());
		return response;
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorInfo handleGeneralException(HttpServletRequest request, Exception ex) {
		System.out.println("RuntimeException handler. Here is the general exception handler." + ex.getClass().getName() + ". message="
				+ getMessage(ex));
		ex.printStackTrace();
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(getMessage(ex));
		response.setError(ex.getClass().getName());
		return response;
	}

	private String getMessage(Exception ex) {
		Throwable e1 = null;
		e1 = getCause(ex);

		if (e1 instanceof org.springframework.security.access.AccessDeniedException) {
			return "Access denied";
		} else if (e1 instanceof javax.validation.ConstraintViolationException) {
			return "System error. Database constraint violation";
		} else if (e1 instanceof org.hibernate.StaleStateException || e1 instanceof ObjectOptimisticLockingFailureException)
			return "Object was either modified or deleted";
		else if (e1.getMessage() != null
				&& e1.getMessage().contains("Cannot add or update a child row: a foreign key constraint fails"))
			return "A child object reference does not exist in database";
		else if (e1 instanceof NullPointerException)
			return "System error. Null pointer exception";
		else if (e1.getMessage() != null && e1.getMessage()
				.startsWith("Required MultipartFile parameter") && e1.getMessage()
				.endsWith("is not present") )
			return "No file parameter provided";

		else
			return e1.getMessage();
	}

	private Throwable getCause(Exception ex) {
		Throwable e1 = null, e2 = null;
		e1 = ex.getCause();
		if (e1 != null)
			e2 = e1.getCause();
		else
			e1 = ex;
		while (e2 != null && e1 != e2) {
			e1 = e2;
			if (e1 != null)
				e2 = e1.getCause();
			if (e2 != null && e2.getMessage()
					.startsWith("Cannot delete or update a parent row: a foreign key constraint fails"))
				return new Exception("Cannot delete/update instance: Already or still in use");
		}
		return e1;
	}
	protected BufferedImage generatePngFromSvg(File file, Integer width, Integer height) throws Exception {
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

		return ImageIO.read(new ByteArrayInputStream(ret));
	}
	
	protected void rdmTimeRdmSuccess() throws Exception {
		boolean RDM_TIME = true;
		boolean RDM_SUCCESS = true;

		RDM_TIME = false;
		RDM_SUCCESS = false;

		if (RDM_TIME){
				Random rand = new Random();
				int random = rand.nextInt(100);
				Uninterruptibles.sleepUninterruptibly(50 * random, TimeUnit.MILLISECONDS);
				if (RDM_SUCCESS && random > 50)
					throw new Exception("Random Rejection"); //
		}
	}

}
