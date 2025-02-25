package com.iss.renterscore.authentication.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
@Getter
public class InvalidTokenRequestException extends RuntimeException {

	private final String tokenType;
	private final String token;
	private final String message;
	public InvalidTokenRequestException(String tokenType, String token, String message) {
		super(String.format("Invalid [%s] token [%s]: %s", token, tokenType, message));
		this.tokenType = tokenType;
		this.token = token;
		this.message = message;
	}
	
	

}
