package com.iss.renterscore.authentication.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public class ResourceAlreadyInUseException extends RuntimeException{

	private final String resourceName;
	private final String fieldName;
	private final Object fieldValue;
	public ResourceAlreadyInUseException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s already in use with %s : '%s'", resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

}
