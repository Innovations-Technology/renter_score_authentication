package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class LoginRequest {

	@NotNull(message = "Login Email can be null but not blank")
	private String email;

	@NotNull(message = "Login password cannot be blank")
	private String password;
	
	@JsonProperty("device_id")
	private String deviceId;
}
