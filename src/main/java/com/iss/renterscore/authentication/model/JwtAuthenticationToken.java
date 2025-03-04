package com.iss.renterscore.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter @Setter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private String token;
	
	public JwtAuthenticationToken(Object principal, Object credentials, String token) {
		super(null, null);
		this.token = token;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}
	
	
}
