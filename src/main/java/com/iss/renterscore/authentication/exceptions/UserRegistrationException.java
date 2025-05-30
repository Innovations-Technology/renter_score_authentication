package com.iss.renterscore.authentication.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
@Getter
public class UserRegistrationException extends RuntimeException{

	private final String user;
	private final String message;

	public UserRegistrationException(String user, String message) {
		super(String.format("Failed to register User [%s]: '%s'", user, message));
		this.user = user;
		this.message = message;
	}
	

}
