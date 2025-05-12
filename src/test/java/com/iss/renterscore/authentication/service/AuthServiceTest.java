package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.exceptions.TokenRefreshException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.LoginRequest;
import com.iss.renterscore.authentication.payloads.RegistrationRequest;
import com.iss.renterscore.authentication.payloads.TokenRefreshRequest;
import com.iss.renterscore.authentication.securityconfig.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "app.token.refresh.duration=60000")
@TestPropertySource(properties = "app.token.email.verification.duration=60000")
 class AuthServiceTest {


    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private Users testUser;
    private RefreshToken refreshToken;
    private RegistrationRequest registrationRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);

        registrationRequest = new RegistrationRequest("John", "Doe", "test@example.com", "password", PropertyRole.ROLE_AGENT);
        loginRequest = new LoginRequest("test@example.com", "password", "device123");

        refreshToken = new RefreshToken();
        refreshToken.setUser(testUser);
        refreshToken.setToken("sampleRefreshToken");
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
    }

    @Test
    void testRegisterUser_Success() {
        when(userService.existsByEmail("test@example.com")).thenReturn(false);
        when(userService.createUser(registrationRequest)).thenReturn(testUser);
        when(userService.createUserProfile(registrationRequest)).thenReturn(new UserProfile());
        when(userService.save(any(Users.class))).thenReturn(testUser);

        Optional<Users> registeredUser = authService.registerUser(registrationRequest);

        assertTrue(registeredUser.isPresent());
        assertEquals("test@example.com", registeredUser.get().getEmail());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(ResourceAlreadyInUseException.class, () -> authService.registerUser(registrationRequest));
    }

    @Test
    void testAuthenticateUser_Success() {
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);

        Optional<Authentication> result = authService.authenticateUser(loginRequest);

        assertTrue(result.isPresent());
    }

    @Test
    void testConfirmEmailRegistration_Success() {
        testUser.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);
        when(userService.findByEmailToken("validToken")).thenReturn(Optional.of(testUser));

        Optional<Users> confirmedUser = authService.confirmEmailRegistration("validToken");

        assertTrue(confirmedUser.isPresent());
        assertEquals(EmailVerificationStatus.STATUS_VERIFIED, confirmedUser.get().getEmailVerificationStatus());
    }

    @Test
    void testConfirmEmailRegistration_TokenNotFound_ThrowsException() {
        when(userService.findByEmailToken("invalidToken")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.confirmEmailRegistration("invalidToken"));
    }

    @Test
    void testRecreateRegistrationToken_UserNotVerified_Success() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userService.updateNewTokenWithExpiry(testUser)).thenReturn(testUser);

        Optional<Users> result = authService.recreateRegistrationToken("test@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void testCreateAndPersistRefreshToken_Success() {
        Authentication authMock = mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(testUser);

        when(refreshTokenService.createRefreshToken()).thenReturn(refreshToken);
        when(refreshTokenService.save(any(RefreshToken.class))).thenReturn(refreshToken);

        Optional<RefreshToken> result = authService.createAndPersistRefreshToken(authMock, loginRequest);

        assertTrue(result.isPresent());
        assertEquals("sampleRefreshToken", result.get().getToken());
    }

    @Test
    void testRefreshJwtToken_Success() {
        when(refreshTokenService.findByToken("sampleRefreshToken")).thenReturn(Optional.of(refreshToken));
        doNothing().when(refreshTokenService).verifyExpiration(refreshToken);
        doNothing().when(refreshTokenService).increaseCount(refreshToken);
        when(tokenProvider.generateTokenByUser(testUser)).thenReturn("newJwtToken");

        Optional<String> result = authService.refreshJwtToken(new TokenRefreshRequest("sampleRefreshToken"));

        assertTrue(result.isPresent());
        assertEquals("newJwtToken", result.get());
    }

    @Test
    void testRefreshJwtToken_TokenNotFound_ThrowsException() {
        when(refreshTokenService.findByToken("invalidToken")).thenReturn(Optional.empty());

        assertThrows(TokenRefreshException.class, () -> authService.refreshJwtToken(new TokenRefreshRequest("invalidToken")));
    }

    @Test
    void testGenerateTokenByUser() {
        when(tokenProvider.generateTokenByUser(testUser)).thenReturn("jwtToken123");

        String token = authService.generateTokenByUser(testUser);

        assertEquals("jwtToken123", token);
    }

    @Test
    void testGenerateToken() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(tokenProvider.generateToken(userDetails)).thenReturn("jwtTokenXYZ");

        String token = authService.generateToken(userDetails);

        assertEquals("jwtTokenXYZ", token);
    }
}
