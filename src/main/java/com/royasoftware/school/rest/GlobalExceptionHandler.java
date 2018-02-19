package com.royasoftware.school.rest;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
	@ExceptionHandler(MultipartException.class)
	@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
	public @ResponseBody ErrorInfo handleSizeLimitExceededException(HttpServletRequest request, Exception ex) {
		System.out.println("Ok now. Here is the SizeLimitExceededException handler." + ex.getClass().getName()
				+ ". message=" + ex.getMessage());
		ErrorInfo response = new ErrorInfo();
		response.setUrl(request.getRequestURL().toString());
		response.setErrorDescription("Size limit Violation");
		return response;
	}
    
}