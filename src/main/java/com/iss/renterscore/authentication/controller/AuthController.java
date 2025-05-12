package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.events.OnRegenerateEmailVerificationEvent;
import com.iss.renterscore.authentication.events.OnUserRegistrationCompleteEvent;
import com.iss.renterscore.authentication.exceptions.InvalidTokenRequestException;
import com.iss.renterscore.authentication.exceptions.TokenRefreshException;
import com.iss.renterscore.authentication.exceptions.UserLoginException;
import com.iss.renterscore.authentication.exceptions.UserRegistrationException;
import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.RefreshToken;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.payloads.*;
import com.iss.renterscore.authentication.securityconfig.JwtTokenProvider;
import com.iss.renterscore.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;


@CrossOrigin(origins = "https://renterscore.live", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher applicationEventPublisher;


    @GetMapping("/hello")
    public ResponseEntity<Object> helloGreeting() {
        return ResponseEntity.ok("<h1>Welcome to Renter Score Application</h1>" +
                "<br /><br /><p><h3>This application is to enhance the Properties' solutions. </h3></p>");
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
        return authService.registerUser(request)
                .map( users -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auth/registration_confirmation");
                    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    OnUserRegistrationCompleteEvent onUserRegistrationCompleteEvent =
                            new OnUserRegistrationCompleteEvent(urlBuilder, users, baseUrl);
                    applicationEventPublisher.publishEvent(onUserRegistrationCompleteEvent);
                    logger.info("Registered User returned: {}", users);
                    return ResponseEntity.ok(new ApiResponse("User registered successfully. Check your email for verification", true));

                }).orElseThrow(() -> new UserRegistrationException(request.getEmail(), "Missing user Data in record"));
    }

    @GetMapping("/registration_confirmation")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        return authService.confirmEmailRegistration(token)
                .map(users -> ResponseEntity.ok(new ApiResponse("User's email verified successfully", true)))
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", token, "Failed to verify. Please request a new email verification"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyRegistration(@Valid @RequestBody VerifyTokenRequest request) {
        return authService.confirmEmailRegistration(request.getToken())
                .map(users -> ResponseEntity.ok(new ApiResponse("User's email verified successfully", true)))
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", request.getToken(), "Failed to verify. Please request a new email verification"));
    }

    @GetMapping("/resend_registration_token")
    public ResponseEntity<?> resendRegistrationToken(@RequestParam("token") String existingToken) {
        Users users = authService.recreateRegistrationToken(existingToken)
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken, "User already verified."));
        return Optional.ofNullable(users)
                .map(registeredUser -> {
                    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auth/registration_confirmation");
                    OnRegenerateEmailVerificationEvent regenerateEmailVerificationEvent =
                            new OnRegenerateEmailVerificationEvent(urlBuilder, registeredUser, baseUrl);
                    applicationEventPublisher.publishEvent(regenerateEmailVerificationEvent);
                    return ResponseEntity.ok(new ApiResponse("Email verification token resent successfully", true));

                }).orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", existingToken, "No user associated with this request."));

    }

    @PostMapping("/resend_verification_token")
    public ResponseEntity<?> resendRegistrationToken(@Valid @RequestBody EmailVerificationTokenRequest request) {
        Users users = authService.recreateRegistrationToken(request.getEmail())
                .orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", request.getEmail(), "User already verified."));
        return Optional.ofNullable(users)
                .map(registeredUser -> {
                    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/auth/registration_confirmation");
                    OnRegenerateEmailVerificationEvent regenerateEmailVerificationEvent =
                            new OnRegenerateEmailVerificationEvent(urlBuilder, registeredUser, baseUrl);
                    applicationEventPublisher.publishEvent(regenerateEmailVerificationEvent);
                    return ResponseEntity.ok(new ApiResponse("Email verification token resent successfully", true));

                }).orElseThrow(() -> new InvalidTokenRequestException("Email Verification Token", request.getEmail(), "No user associated with this request."));

    }

    @GetMapping("/check_email_in_use")
    public ResponseEntity<ApiResponse> checkEmailInUse(@RequestParam("email") String email) {
        Boolean emailExists = authService.emailAlreadyExist(email);
        return ResponseEntity.ok(new ApiResponse(emailExists.toString(), true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authService.authenticateUser(loginRequest)
                .orElseThrow(() -> new UserLoginException("Couldn't login user :[" + loginRequest + "]"));
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        logger.info("Logged in user returned: {}", customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authService.createAndPersistRefreshToken(authentication, loginRequest)
                .map(RefreshToken::getToken)
                .map( refreshToken -> {
                    String jwtToken = authService.generateToken(customUserDetails);
                    return ResponseEntity.ok(new JwtAuthenticationResponse(jwtToken, refreshToken, jwtTokenProvider.getExpiryDuration()));

                }).orElseThrow(() -> new UserLoginException("Couldn't create token for Login: [" + loginRequest + "]"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshJwtToken(@Valid @RequestBody TokenRefreshRequest refreshRequest) {
        return authService.refreshJwtToken(refreshRequest)
                .map(updatedToken -> {
                    String refreshToken = refreshRequest.getRefreshToken();
                    logger.info("Created a new JWT Auth token:{}", updatedToken);
                    return ResponseEntity.ok(new JwtAuthenticationResponse(updatedToken, refreshToken, jwtTokenProvider.getExpiryDuration()));
                }).orElseThrow(() -> new TokenRefreshException(refreshRequest.getRefreshToken(), "Unexpected error during token refresh. Please login again."));

    }

}
