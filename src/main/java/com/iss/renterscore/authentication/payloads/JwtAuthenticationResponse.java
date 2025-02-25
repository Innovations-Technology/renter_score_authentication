package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter @Setter
public class JwtAuthenticationResponse implements Serializable {
	

	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	@JsonProperty("expired_duration")
	private Long expiryDuration;
	
	public JwtAuthenticationResponse(String accessToken, String refreshToken, Long expiryDuration) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiryDuration = expiryDuration;
		tokenType = "Bearer ";
	}
	
	

}
