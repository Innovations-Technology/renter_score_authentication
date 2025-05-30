package com.iss.renterscore.authentication.events;

import com.iss.renterscore.authentication.securityconfig.JwtTokenProvider;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class LoggedOutJwtTokenCache {

	private static final Logger logger = LoggerFactory.getLogger(LoggedOutJwtTokenCache.class);
	
	private final ExpiringMap<String, OnUserLogoutSuccessEvent> tokenEventMap;
	private final JwtTokenProvider tokenProvider;
	
	@Autowired
	public LoggedOutJwtTokenCache(@Value("${app.cache.logoutToken.maxSize}") int maxSize, JwtTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
		tokenEventMap = ExpiringMap.builder().variableExpiration().maxSize(maxSize).build();
	}
	
	public void markLogoutEventForToken(OnUserLogoutSuccessEvent event) {
		String token = event.getToken();
		if (tokenEventMap.containsKey(token)) {
            logger.info("Log out token for user {} is already present in the cache", event.getEmail());
		}else {
			Date tokenExpiryDate = tokenProvider.getTokenExpiryFromJwt(token);
			long ttlForToken = getTTLForToken(tokenExpiryDate);
			logger.info("Logout token cache set for [{}] with a TTL of [{}] seconds. Token is due expiry at [{}]", event.getEmail(), ttlForToken, tokenExpiryDate);
			tokenEventMap.put(token, event, ttlForToken, TimeUnit.SECONDS);
		}
	}
	
	public OnUserLogoutSuccessEvent getLogoutEventForToken(String token) {
		return tokenEventMap.get(token);
	}
	private long getTTLForToken(Date date) {
		long secondAtExpiry = date.toInstant().getEpochSecond();
		long secondAtLogout = Instant.now().getEpochSecond();
		return Math.max(0, secondAtExpiry - secondAtLogout);
	}
	
}
