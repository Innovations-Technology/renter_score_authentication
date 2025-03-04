package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.model.Mail;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

	private JavaMailSender mailSender;
	private Configuration templateConfiguration;
	
	@Value("${app.templates.location}")
	private String basePackagePath;
	
	@Value("${spring.mail.username}")
	private String mailFrom;
	
	@Value("${spring.mail.sendername}")
	private String mailFromName;
	
	@Value("${app.token.password.reset.duration}")
	private Long expiration;
	
	@Autowired
	public MailService(JavaMailSender mailSender, Configuration templateConfiguration) {
		this.mailSender = mailSender;
		this.templateConfiguration = templateConfiguration;
	}

	/*	User Registration complete Action  */
	public void sendEmailVerification(String emailVerificationUrl, String to, String toName, String baseUrl) throws IOException, TemplateException, MessagingException {
		
		Mail mail = new Mail();
		mail.setSubject("User Email Verification");
		mail.setTo(to);
		mail.setFrom(mailFrom);
		mail.getModel().put("userName", toName);
		mail.getModel().put("title", "Renter Score Application");
		mail.getModel().put("baseUrl", baseUrl);
		mail.getModel().put("userEmailTokenVerificationLink", emailVerificationUrl);
		templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
		Template template = templateConfiguration.getTemplate("email-verification.ftlh");
		String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
		mail.setContent(mailContent);
		send(mail);
	}
	
	public void sendResetLink(String resetPasswordLink, String to, String toName, String baseUrl) throws IOException, TemplateException, MessagingException {
		long expirationInMinutes = TimeUnit.MILLISECONDS.toMinutes(expiration);
		String expirationInMinutesString = Long.toString(expirationInMinutes);
		Mail mail = new Mail();
		mail.setSubject("Password Reset Link");
		mail.setTo(to);
		mail.setFrom(mailFrom);
		mail.getModel().put("userName", toName);
		mail.getModel().put("title", "Renter Score Application");
		mail.getModel().put("baseUrl", baseUrl);
		mail.getModel().put("userResetPasswordLink", resetPasswordLink);
		mail.getModel().put("expiration", expirationInMinutesString);
		
		templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
		Template template = templateConfiguration.getTemplate("reset-link.ftlh");
		String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
		mail.setContent(mailContent);
		send(mail);
	}
	
	public void sendAccountChangeEmail(String action, String actionStatus, String to, String toName, String baseUrl) throws IOException, TemplateException, MessagingException {
		Mail mail = new Mail();
		mail.setSubject("Account Status Change");
		mail.setTo(to);
		mail.setFrom(mailFrom);
		mail.getModel().put("userName", toName);
		mail.getModel().put("action", action);
		mail.getModel().put("title", "Renter Score Application");
		mail.getModel().put("baseUrl", baseUrl);
		mail.getModel().put("actionStatus", actionStatus);
		templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
		Template template = templateConfiguration.getTemplate("account-activity-change.ftlh");
		String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
		mail.setContent(mailContent);
		send(mail);
	}

	public void send(Mail mail) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());
		if (mail.getTo().contains(",")) {
			helper.setTo(mail.getTo().split(","));
		}else {
			helper.setTo(mail.getTo());
		}
		
		helper.setText(mail.getContent(), true);
		helper.setSubject(mail.getSubject());
		helper.setPriority(1);
		helper.setFrom(new InternetAddress(mailFrom, mailFromName));
		mailSender.send(message);
	}

}
