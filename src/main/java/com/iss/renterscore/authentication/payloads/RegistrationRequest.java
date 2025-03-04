package com.iss.renterscore.authentication.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class RegistrationRequest {

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	@JsonProperty("email")
	private String email;
	
	@JsonProperty("password")
	private String password;

	@JsonProperty("role")
	private String role;

}
