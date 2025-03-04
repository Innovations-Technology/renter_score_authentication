package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class UpdatePasswordRequest {

	@JsonProperty("old_password")
	@NotBlank(message = "Old password must not be blank")
	private String oldPassword;
	
	@JsonProperty("new_password")
	@NotBlank(message = "New password must not be blank")
	private String newPassword;
	
}
