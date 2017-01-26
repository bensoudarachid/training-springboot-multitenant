package com.royasoftware.rest;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.multipart.MultipartException;

import com.google.common.net.InternetDomainName;
import com.royasoftware.TenantContext;

/**
 * Base of all controllers
 */
public class BaseController {
	
	/**
	 * The Logger for this class.
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private HttpServletRequest request;


//	ResponseEntity<Map<String, Object>>
	@ExceptionHandler(NoResultException.class)
	public @ResponseBody ExceptionJSONInfo handleNoResultException(NoResultException noResultException,
			HttpServletRequest request) {

		logger.info("handleNoResultException");

//		ExceptionAttributes exceptionAttributes = new DefaultExceptionAttributes();
//
//		Map<String, Object> responseBody = exceptionAttributes.getExceptionAttributes(noResultException, request,
//				HttpStatus.NOT_FOUND);

		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(getMessage(noResultException));
		response.setError(noResultException.getClass().getName());

		logger.info("handleNoResultException");
		return response;
//		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.NOT_FOUND);
	}
	// @ExceptionHandler(NoResultException.class)
	// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	// public @ResponseBody ExceptionJSONInfo handleNoResultException(
	// NoResultException noResultException, HttpServletRequest request) {
	//
	// ExceptionJSONInfo response = new ExceptionJSONInfo();
	// response.setUrl(request.getRequestURL().toString());
	// response.setMessage("No result found");
	// return response;
	// }

	//ResponseEntity<Map<String, Object>>
	@ExceptionHandler(Exception.class)
	public @ResponseBody ExceptionJSONInfo handleException(Exception exception, HttpServletRequest request) {

//		logger.error("Base controller Exception handler > handleException");
		logger.error("Exception handler. ", getMessage(exception));

//		ExceptionAttributes exceptionAttributes = new DefaultExceptionAttributes();
//
//		Map<String, Object> responseBody = exceptionAttributes.getExceptionAttributes(exception, request,
//				HttpStatus.INTERNAL_SERVER_ERROR);

		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(getMessage(exception));
		response.setError(exception.getClass().getName());
//		logger.error("< handleException");
		return response;

//		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	// @ExceptionHandler(Exception.class)
	// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	// public @ResponseBody ExceptionJSONInfo handleException(
	// Exception exception, HttpServletRequest request) {
	//
	// ExceptionJSONInfo response = new ExceptionJSONInfo();
	// response.setUrl(request.getRequestURL().toString());
	// response.setMessage(exception.getMessage());
	// return response;
	// }
//	@ExceptionHandler(SizeLimitExceededException.class)
//	@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
//	public @ResponseBody ExceptionJSONInfo handleSizeLimitExceededException(HttpServletRequest request, Exception ex) {
//		System.out.println("Ok now. Here is the SizeLimitExceededException handler." + ex.getClass().getName()
//				+ ". message=" + ex.getMessage());
//		ExceptionJSONInfo response = new ExceptionJSONInfo();
//		response.setUrl(request.getRequestURL().toString());
//		response.setErrorDescription("Size limit Violation");
//		return response;
//	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ExceptionJSONInfo handleDataIntegrityException(HttpServletRequest request, Exception ex) {
		System.out.println("DataIntegrityViolationException handler. Here is the DataIntegrityViolationException handler." + ex.getClass().getName()
				+ ". message=" + getMessage(ex));
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription("Data Integrity Violation");
		return response;
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ExceptionJSONInfo handleGeneralException(HttpServletRequest request, Exception ex) {
		System.out.println("RuntimeException handler. Here is the general exception handler." + ex.getClass().getName() + ". message="
				+ getMessage(ex));
		ex.printStackTrace();
		ExceptionJSONInfo response = new ExceptionJSONInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription(getMessage(ex));
		response.setError(ex.getClass().getName());
		return response;
	}

	private String getMessage(Exception ex) {
		Throwable e1 = null;
		e1 = getCause(ex);

		if (e1 instanceof javax.validation.ConstraintViolationException) {
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
//	protected String getSubdomain() throws Exception{
//		String site = request.getServerName();
//        String domain = InternetDomainName.from(request.getServerName()).topPrivateDomain().toString();
//        String subdomain = site.replaceAll(domain, "");
//        subdomain = subdomain.substring(0, subdomain.length() - 1);
//        logger.info("Base controller. Subdomain = " + subdomain);
//        if(subdomain.contains("."))
//        	throw new Exception("Mammaaaaa! Sub with point is not allowed");
//		TenantContext.setCurrentTenant(subdomain);
//      return subdomain;
//	}
}
