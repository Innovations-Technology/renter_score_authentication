package com.iss.renterscore.authentication.securityconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditRef")
public class AuditConfig  {

	public AuditAware auditRef() {
		return new AuditAware();
	}

}
