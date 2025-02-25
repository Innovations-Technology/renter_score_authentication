package com.iss.renterscore.authentication.events.listeners;

import com.iss.renterscore.authentication.events.LoggedOutJwtTokenCache;
import com.iss.renterscore.authentication.events.OnUserLogoutSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OnUserLogoutSuccessEventListener implements ApplicationListener<OnUserLogoutSuccessEvent> {

	private static final Logger logger = LoggerFactory.getLogger(OnUserLogoutSuccessEventListener.class);
	
	private final LoggedOutJwtTokenCache tokenCache;

	@Autowired
	public OnUserLogoutSuccessEventListener(LoggedOutJwtTokenCache tokenCache) {
		this.tokenCache = tokenCache;
	}

	@Override
	@Async
	public void onApplicationEvent(OnUserLogoutSuccessEvent event) {
        String deviceId = event.getLogoutRequest().getDeviceId();
        logger.info("Log out success event received for user[{}] for device [{}]", event.getEmail(), deviceId);
        tokenCache.markLogoutEventForToken(event);
    }
	
}
