package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.exceptions.TokenRefreshException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.LoginRequest;
import com.iss.renterscore.authentication.payloads.RegistrationRequest;
import com.iss.renterscore.authentication.payloads.TokenRefreshRequest;
import com.iss.renterscore.authentication.securityconfig.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenService passwordResetTokenService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public Optional<Users> registerUser(RegistrationRequest request) {
        String email = request.getEmail();
        if (Boolean.TRUE.equals(emailAlreadyExist(email))) {
            throw new ResourceAlreadyInUseException("Email", "Address", email);
        }
        Users newUser = userService.createUser(request);
        UserProfile profile = userService.createUserProfile(request);
        newUser.setProfile(profile);
     //   Users registeredUser = userService.save(newUser);

        return Optional.ofNullable(newUser);
    }

    public Boolean emailAlreadyExist(String email) {
        return userService.existsByEmail(email);
    }

    public Optional<Authentication> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        return Optional.ofNullable(authentication);
    }

    public Optional<Users> confirmEmailRegistration(String emailToken) {
        Users users = userService.findByEmailToken(emailToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "Email verification token not found", emailToken));
        if (users.getEmailVerificationStatus() == EmailVerificationStatus.STATUS_VERIFIED) {
            return Optional.of(users);
        }
        userService.verifyExpiration(users);
        users.setEmailVerificationStatus(EmailVerificationStatus.STATUS_VERIFIED);
        userService.save(users);

        return Optional.of(users);
    }

    public Optional<Users> recreateRegistrationToken(String email) {
        Optional<Users> userOpt = userService.findByEmail(email);
        userOpt.orElseThrow(() -> new ResourceNotFoundException("Token", "Existing email verification", email));
        boolean userAlreadyVerified = userOpt.map(Users::getEmailVerificationStatus).filter(status -> status == EmailVerificationStatus.STATUS_VERIFIED).isPresent();
        if (userAlreadyVerified) {
            return Optional.empty();
        }
        return userOpt.map(userService::updateNewTokenWithExpiry);
    }

    /* Create Refresh token to refresh JWT token  */
    public Optional<RefreshToken> createAndPersistRefreshToken(Authentication authentication, LoginRequest loginRequest) {

        Users currentUser = (Users) authentication.getPrincipal();
        String deviceId = loginRequest.getDeviceId();
        if (deviceId != null) {
            refreshTokenService.deleteByUserIdAndDeviceId(currentUser, deviceId);
        }
        RefreshToken refreshToken = refreshTokenService.createRefreshToken();
        refreshToken.setUser(currentUser);
        refreshToken.setDeviceId(deviceId);
        refreshToken = refreshTokenService.save(refreshToken);
        return Optional.ofNullable(refreshToken);
    }

    /* Refresh JWT token */
    public Optional<String> refreshJwtToken(TokenRefreshRequest refreshRequest) {

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(refreshRequest.getRefreshToken());
        refreshTokenOpt.orElseThrow(() -> new TokenRefreshException(refreshRequest.getRefreshToken(),
                "Missing refresh token in records. Please login again."));
        refreshTokenOpt.ifPresent(refreshTokenService::verifyExpiration);
        refreshTokenOpt.ifPresent(refreshTokenService::increaseCount);
        return refreshTokenOpt.map(RefreshToken::getUser)
                .map(this::generateTokenByUser);
    }

    /* Generate a JWT token for the validated user */
    public String generateTokenByUser(Users users) {
        return tokenProvider.generateTokenByUser(users);
    }

    /* Generate a JWT token for the validated logged-in user */
    public String generateToken(CustomUserDetails userDetails) {
        return tokenProvider.generateToken(userDetails);
    }


}
