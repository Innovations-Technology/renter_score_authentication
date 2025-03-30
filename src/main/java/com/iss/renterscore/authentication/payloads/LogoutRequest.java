package com.iss.renterscore.authentication.payloads;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class LogoutRequest {

	@Valid
	private String deviceId;
	
}
