package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
