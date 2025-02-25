package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ApiResponse {

	private final String data;
	private final Boolean success;
	private final String timestamp;
	private final String cause;
	private final String path;

	public ApiResponse(String data, Boolean success, String cause, String path) {
		this.data = data;
		this.success = success;
		this.cause = cause;
		this.path = path;
		this.timestamp = Instant.now().toString();
	}
	public ApiResponse(String data, Boolean success) {
		this.data = data;
		this.success = success;
		this.cause = null;
		this.path = null;
		this.timestamp = Instant.now().toString();
	}
	
	
	
}
