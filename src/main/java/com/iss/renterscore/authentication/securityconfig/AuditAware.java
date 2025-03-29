package com.iss.renterscore.authentication.securityconfig;

import com.iss.renterscore.authentication.model.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditRef")
public class AuditAware implements AuditorAware<Long> {

	@NonNull
	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
			return Optional.of(0L);
		}
		return Optional.ofNullable(((CustomUserDetails) authentication.getPrincipal()).getId());
	}

}
