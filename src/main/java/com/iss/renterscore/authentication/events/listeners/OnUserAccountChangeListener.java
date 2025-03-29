package com.iss.renterscore.authentication.events.listeners;

import com.iss.renterscore.authentication.events.OnUserAccountChangeEvent;
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
public class OnUserAccountChangeListener implements ApplicationListener<OnUserAccountChangeEvent> {

	private static final Logger logger = LoggerFactory.getLogger(OnUserAccountChangeListener.class);
	
	private final MailService mailService;

	@Autowired
	public OnUserAccountChangeListener(MailService mailService) {
		this.mailService = mailService;
	}

	@Override
	@Async
	public void onApplicationEvent(@NonNull OnUserAccountChangeEvent event) {
		sendAccountChangeEmail(event);
	}
	
	private void sendAccountChangeEmail(OnUserAccountChangeEvent event) {
		Users users = event.getUser();
		String action = event.getAction();
		String actionStatus = event.getActionStatus();
		String recipientAddress = users.getEmail();
		String baseUrl = event.getBaseUrl();
		try {
			mailService.sendAccountChangeEmail(action, actionStatus, recipientAddress, users.getProfile().getFirstName(), baseUrl);
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error(e.getMessage());
			throw new MailSendException(recipientAddress, "Account Change Mail");
		}
	}
	
}
