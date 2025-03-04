package com.iss.renterscore.authentication.events;


import com.iss.renterscore.authentication.payloads.LogoutRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.Date;

@Getter @Setter
public class OnUserLogoutSuccessEvent extends ApplicationEvent {

	private final String email;
	private final String token;
	private final transient LogoutRequest logoutRequest;
	private final Date eventTime;
	public OnUserLogoutSuccessEvent(String email, String token, LogoutRequest logoutRequest) {
		super(email);
		this.email = email;
		this.token = token;
		this.logoutRequest = logoutRequest;
		this.eventTime = Date.from(Instant.now());
	}
	
	
}
