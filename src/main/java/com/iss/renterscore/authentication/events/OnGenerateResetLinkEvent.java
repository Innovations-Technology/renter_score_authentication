package com.iss.renterscore.authentication.events;

import com.iss.renterscore.authentication.model.PasswordResetToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter @Setter
public class OnGenerateResetLinkEvent extends ApplicationEvent{

	private transient UriComponentsBuilder redirectUrl;
	private transient PasswordResetToken passwordResetToken;
	private String baseUrl;
	
	public OnGenerateResetLinkEvent(UriComponentsBuilder redirectUrl,
			PasswordResetToken passwordResetToken, String baseUrl) {
		super(passwordResetToken);
		this.redirectUrl = redirectUrl;
		this.passwordResetToken = passwordResetToken;
		this.baseUrl = baseUrl;
	}
	
}
