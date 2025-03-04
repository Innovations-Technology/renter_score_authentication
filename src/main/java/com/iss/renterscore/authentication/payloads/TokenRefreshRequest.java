package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TokenRefreshRequest implements Serializable {

	@NotBlank(message = "Refresh token cannot be blank")
	@JsonProperty("refresh_token")
	private String refreshToken;
	
}
