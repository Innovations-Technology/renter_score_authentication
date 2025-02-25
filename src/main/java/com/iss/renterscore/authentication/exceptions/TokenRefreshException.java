package com.iss.renterscore.authentication.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
@Getter
public class TokenRefreshException extends RuntimeException {

	private final String token;
	private final String message;
	public TokenRefreshException(String token, String message) {
		super(String.format("Couldn't refresh token for [%s]: [%s]", token, message));
		this.token = token;
		this.message = message;
	}
	
}
