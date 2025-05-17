package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.exceptions.InvalidTokenRequestException;
import com.iss.renterscore.authentication.exceptions.UserLoginException;
import com.iss.renterscore.authentication.exceptions.UserRegistrationException;
import com.iss.renterscore.authentication.model.PropertyRole;
import com.iss.renterscore.authentication.model.UserRole;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.payloads.*;
import com.iss.renterscore.authentication.securityconfig.JwtTokenProvider;
import com.iss.renterscore.authentication.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class AuthControllerTest {
    @Mock
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;


    @InjectMocks
    private AuthController authController;

    private RegistrationRequest registrationRequest;
    private LoginRequest loginRequest;
    private TokenRefreshRequest refreshRequest;
    private VerifyTokenRequest verifyTokenRequest;
    private Users user;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("Test");
        registrationRequest.setLastName("User");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setRole(PropertyRole.ROLE_LANDLORD);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setDeviceId("Test Device");

        refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken("refresh-token");

        EmailVerificationTokenRequest emailVerificationRequest = new EmailVerificationTokenRequest();
        emailVerificationRequest.setEmail("test@example.com");

        verifyTokenRequest = new VerifyTokenRequest();
        verifyTokenRequest.setToken("verification-token");

        user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUserRole(UserRole.ROLE_USER);
    }

    @Test
    void helloGreeting_ReturnsWelcomeMessage() {
        // Act
        ResponseEntity<Object> response = authController.helloGreeting();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Welcome to Renter Score Application"));
    }

    @Test
    void registerUser_Failure_ThrowsUserRegistrationException() {
        // Arrange
        when(authService.registerUser(registrationRequest)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserRegistrationException.class, () -> authController.registerUser(registrationRequest));
    }

    @Test
    void confirmRegistration_Success_ReturnsSuccessResponse() {
        // Arrange
        when(authService.confirmEmailRegistration("valid-token")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = authController.confirmRegistration("valid-token");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertEquals("User's email verified successfully",
                ((ApiResponse) response.getBody()).getData());
    }

    @Test
    void confirmRegistration_Failure_ThrowsInvalidTokenRequestException() {
        // Arrange
        when(authService.confirmEmailRegistration("invalid-token")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenRequestException.class, () -> authController.confirmRegistration("invalid-token"));
    }

    @Test
    void verifyRegistration_Success_ReturnsSuccessResponse() {
        // Arrange
        when(authService.confirmEmailRegistration(verifyTokenRequest.getToken())).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = authController.verifyRegistration(verifyTokenRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertEquals("User's email verified successfully",
                ((ApiResponse) response.getBody()).getData());
    }


    @Test
    void authenticateUser_Failure_ThrowsUserLoginException() {
        when(authService.authenticateUser(loginRequest)).thenReturn(Optional.empty());

        assertThrows(UserLoginException.class, () -> authController.authenticateUser(loginRequest));
    }


    @Test
    void refreshJwtToken_Success_ReturnsNewJwtResponse() {
        // Arrange
        when(authService.refreshJwtToken(refreshRequest))
                .thenReturn(Optional.of("new-jwt-token"));
        when(jwtTokenProvider.getExpiryDuration()).thenReturn(3600L);

        // Act
        ResponseEntity<?> response = authController.refreshJwtToken(refreshRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(JwtAuthenticationResponse.class, response.getBody());
        JwtAuthenticationResponse jwtResponse = (JwtAuthenticationResponse) response.getBody();
        assertEquals("new-jwt-token", jwtResponse.getAccessToken());
        assertEquals("refresh-token", jwtResponse.getRefreshToken());
    }
}
