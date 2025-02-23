package com.iss.renterscore.authentication.events;

import com.iss.renterscore.authentication.model.Users;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter @Setter
public class OnRegenerateEmailVerificationEvent extends ApplicationEvent {

	private transient UriComponentsBuilder redirectUrl;
	private Users users;
	private String baseUrl;
	public OnRegenerateEmailVerificationEvent(UriComponentsBuilder redirectUrl, Users users, String baseUrl) {
		super(users);
		this.redirectUrl = redirectUrl;
		this.users = users;
		this.baseUrl = baseUrl;
	}

}
