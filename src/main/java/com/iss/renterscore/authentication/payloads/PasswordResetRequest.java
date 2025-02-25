package com.iss.renterscore.authentication.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class PasswordResetRequest {

	@NotBlank(message = "Password cannot be blank")
	private String password;
	
	@NotBlank(message = "Confirm Password cannot be blank")
	private String confirmPassword;
	
	@NotBlank(message = "Token has to be supplied along with a password reset request")
	private String token;
	
	
}
