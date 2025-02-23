package com.iss.renterscore.authentication.securityconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

@Configuration
@PropertySource("classpath:mail.properties")
@EnableAsync
public class MailConfig {

	@Value("${spring.mail.default-encoding}")
	private String mailDefaultEncoding;
	
	@Value("${spring.mail.host}")
	private String mailHost;
	
	@Value("${spring.mail.username}")
	private String mailUsername;
	
	@Value("${spring.mail.password}")
	private String mailPassword;
	
	@Value("${spring.mail.port}")
	private Integer mailPort;
	
	@Value("${spring.mail.protocol}")
	private String mailProtocol;
	
	@Value("${spring.mail.debug}")
	private String mailDebug;
	
	@Value("${spring.mail.smtp.auth}")
	private String mailSmtpAuth;
	
	@Value("${spring.mail.smtp.starttls.enable}")
	private String mailSmtpStartTls;
	
	@Bean
	@Primary
	public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath("classpath:/templates/");
		return bean;
	}
	
	@Bean
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl mailsender = new JavaMailSenderImpl();
		mailsender.setHost(mailHost);
		mailsender.setDefaultEncoding(mailDefaultEncoding);
		mailsender.setPort(mailPort);
		mailsender.setUsername(mailUsername);
		mailsender.setPassword(mailPassword);
		
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", mailSmtpStartTls);
		javaMailProperties.put("mail.smtp.auth", mailSmtpAuth);
		javaMailProperties.put("mail.transport.protocol", mailProtocol);
		javaMailProperties.put("mail.debug", mailDebug);
		mailsender.setJavaMailProperties(javaMailProperties);
		return mailsender;
	}
}
