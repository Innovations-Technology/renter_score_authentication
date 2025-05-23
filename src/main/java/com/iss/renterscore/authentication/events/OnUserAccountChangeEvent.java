package com.iss.renterscore.authentication.events;

import com.iss.renterscore.authentication.model.Users;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter @Setter
public class OnUserAccountChangeEvent extends ApplicationEvent {
	
	private final transient Users user;
	private final String action;
	private final String actionStatus;
	private final String baseUrl;
	public OnUserAccountChangeEvent(Users user, String action, String actionStatus, String baseUrl) {
		super(user);
		this.user = user;
		this.action = action;
		this.actionStatus = actionStatus;
		this.baseUrl = baseUrl;
	}

}
