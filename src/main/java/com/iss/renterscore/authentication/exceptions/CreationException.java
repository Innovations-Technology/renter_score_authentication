package com.iss.renterscore.authentication.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
@Getter
public class CreationException extends RuntimeException{

	private final String name;
	private final String message;

	public CreationException(String name, String message) {
		super(String.format("Failed to create [%s]: '%s'", name, message));
		this.name = name;
		this.message = message;
	}
	

}
