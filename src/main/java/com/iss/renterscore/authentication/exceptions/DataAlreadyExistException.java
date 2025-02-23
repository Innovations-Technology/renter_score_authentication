package com.iss.renterscore.authentication.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.IM_USED)
@Getter
public class DataAlreadyExistException extends RuntimeException {

	private final String field;
	private final String resource;

	public DataAlreadyExistException(String field, String resource) {
		super(String.format("%s is already exists for  %s", field, resource));
		this.field = field;
		this.resource = resource;
	}

}
