package com.iss.renterscore.authentication.events.listeners;

import com.iss.renterscore.authentication.events.OnRegenerateEmailVerificationEvent;
import com.iss.renterscore.authentication.exceptions.MailSendException;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.service.MailService;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OnRegenerateEmailVerificationListener implements ApplicationListener<OnRegenerateEmailVerificationEvent> {

	private static final Logger logger = LoggerFactory.getLogger(OnRegenerateEmailVerificationListener.class);
	
	private final MailService mailService;

	@Autowired
	public OnRegenerateEmailVerificationListener(MailService mailService) {
		this.mailService = mailService;
	}

	@Override
	@Async
	public void onApplicationEvent(@NonNull OnRegenerateEmailVerificationEvent event) {
		resendEmailVerification(event);
	}
	
	private void resendEmailVerification(OnRegenerateEmailVerificationEvent event) {
		Users users = event.getUsers();
		String recipientAddress = users.getEmail();
		String baseUrl = event.getBaseUrl();
		String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", users.getVerificationToken()).toUriString();
		try {
			mailService.sendEmailVerification(emailConfirmationUrl, recipientAddress, users.getProfile().getFirstName(), baseUrl);
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error(e.getMessage());
			throw new MailSendException(recipientAddress, "Email Verification failed!");
		}
	}
	
}
