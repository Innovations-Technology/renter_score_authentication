package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.model.Mails;
import com.iss.renterscore.authentication.utils.Utils;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.iss.renterscore.authentication.utils.Utils.*;

@Service
@RequiredArgsConstructor
public class MailService {

	private static final Logger logger = LoggerFactory.getLogger(MailService.class);

	private Configuration templateConfiguration;
	
	@Value("${app.templates.location}")
	private String basePackagePath;
	
	@Value("${spring.mail.username}")
	private String mailFrom;
	
	@Value("${spring.mail.sendername}")
	private String mailFromName;
	
	@Value("${app.token.password.reset.duration}")
	private Long expiration;

	@Value("#{systemEnvironment['SENDGRID_API_KEY']}")
	private String sendGridApiKey;
	
	@Autowired
	public MailService(Configuration templateConfiguration) {
		this.templateConfiguration = templateConfiguration;
	}

	/*	User Registration complete Action  */
	public void sendEmailVerification(String emailVerificationUrl, String to, String toName, String baseUrl, String token) throws IOException, TemplateException {
		
		Mails mail = new Mails();
		mail.setSubject(EMAIL_VERIFICATION);
		mail.setTo(to);
		mail.setFrom(mailFrom);
		mail.getModel().put(Utils.USER_NAME, toName);
		mail.getModel().put(TITLE, MAIL_TITLE);
		mail.getModel().put(BASE_URL, baseUrl);
		mail.getModel().put("userEmailTokenVerificationLink", emailVerificationUrl);
		mail.getModel().put("verificationToken", token);
		templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
		Template template = templateConfiguration.getTemplate("email-verification.ftlh");
		String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
		mail.setContent(mailContent);
		send(mail);
	}
	
	public void sendResetLink(String resetPasswordLink, String to, String toName, String baseUrl) throws IOException, TemplateException {
		long expirationInMinutes = TimeUnit.MILLISECONDS.toMinutes(expiration);
		String expirationInMinutesString = Long.toString(expirationInMinutes);
		Mails mail = new Mails();
		mail.setSubject(PASSWORD_RESET_LINK);
		mail.setTo(to);
		mail.setFrom(mailFrom);
		mail.getModel().put(Utils.USER_NAME, toName);
		mail.getModel().put(TITLE, MAIL_TITLE);
		mail.getModel().put(BASE_URL, baseUrl);
		mail.getModel().put("userResetPasswordLink", resetPasswordLink);
		mail.getModel().put("expiration", expirationInMinutesString);
		
		templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
		Template template = templateConfiguration.getTemplate("reset-link.ftlh");
		String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
		mail.setContent(mailContent);
		send(mail);
	}
	
	public void sendAccountChangeEmail(String action, String actionStatus, String to, String toName, String baseUrl) throws IOException, TemplateException {
		Mails mail = new Mails();
		mail.setSubject(ACCOUNT_STATUS);
		mail.setTo(to);
		mail.setFrom(mailFrom);
		mail.getModel().put(Utils.USER_NAME, toName);
		mail.getModel().put("action", action);
		mail.getModel().put(TITLE, MAIL_TITLE);
		mail.getModel().put(BASE_URL, baseUrl);
		mail.getModel().put("actionStatus", actionStatus);
		templateConfiguration.setClassForTemplateLoading(getClass(), basePackagePath);
		Template template = templateConfiguration.getTemplate("account-activity-change.ftlh");
		String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.getModel());
		mail.setContent(mailContent);
		send(mail);
	}

	public void send(Mails mail) {
		checkEnv();
		Email from = new Email(mailFrom, mailFromName);
		Email to = new Email(mail.getTo());
		Content content = new Content("text/html", mail.getContent());
		Mail sendGridMail = new Mail(from, mail.getSubject(), to, content);
		SendGrid sendGrid = new SendGrid(sendGridApiKey);
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(sendGridMail.build());
			Response response = sendGrid.api(request);
			logger.info("SendGrid Response: status={}, body={}, headers={}",
					response.getStatusCode(), response.getBody(), response.getHeaders());
		} catch (IOException e) {
			logger.error("Failed to send email with SendGrid", e);
			throw new RuntimeException("Email sending failed", e);

		}

	}

	@PostConstruct
	public void checkEnv() {
		logger.info("Spring ENV: ");
	}
}
