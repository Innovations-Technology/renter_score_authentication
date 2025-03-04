package com.iss.renterscore.authentication.events.listeners;

import com.iss.renterscore.authentication.events.OnGenerateResetLinkEvent;
import com.iss.renterscore.authentication.exceptions.MailSendException;
import com.iss.renterscore.authentication.model.PasswordResetToken;
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
public class OnGenerateResetLinkEventListener implements ApplicationListener<OnGenerateResetLinkEvent> {

	private static final Logger logger = LoggerFactory.getLogger(OnGenerateResetLinkEventListener.class);
	
	private final MailService mailService;

	@Autowired
	public OnGenerateResetLinkEventListener(MailService mailService) {
		this.mailService = mailService;
	}

	@Override
	@Async
	public void onApplicationEvent(@NonNull OnGenerateResetLinkEvent event) {
		sendResetLink(event);
	}
	
	private void sendResetLink(OnGenerateResetLinkEvent event) {
		PasswordResetToken passwordResetToken = event.getPasswordResetToken();
		Users user = passwordResetToken.getUser();
		String recipientAddress = user.getEmail();
		String baseUrl = event.getBaseUrl();
		String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", passwordResetToken.getToken()).toUriString();
		try {
			mailService.sendResetLink(emailConfirmationUrl, recipientAddress, user.getProfile().getFirstName(), baseUrl);
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error(e.getMessage());
			throw new MailSendException(recipientAddress, "Email Verification");
		}
		
	}
	
}
