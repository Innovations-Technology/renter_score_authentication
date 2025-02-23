package com.iss.renterscore.authentication.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class PasswordResetLinkRequest {

	@NotBlank(message = "Email cannot be blank.")
	private String email;
	
}
