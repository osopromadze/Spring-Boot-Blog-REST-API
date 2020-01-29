package com.sopromadze.blogapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnathorizedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String message;

	public UnathorizedException(String message){
		super(message);
		this.message = message;
	}
	
	public UnathorizedException(String message, Throwable cause){
		super(message, cause);
	}
	
	public String getMessage() {
		return message;
	}

}
