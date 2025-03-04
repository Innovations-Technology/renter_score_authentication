package com.iss.renterscore.authentication.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
@Getter
public class TokenExpiredException extends RuntimeException {

	private final String tokenType;
	private final String token;
	private final String message;
	public TokenExpiredException(String tokenType, String token, String message) {
		super(String.format("Invalid [%s] token [%s]: %s", token, tokenType, message));
		this.tokenType = tokenType;
		this.token = token;
		this.message = message;
	}
	
	
}
