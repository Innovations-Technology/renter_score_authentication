package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.InvalidTokenRequestException;
import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.RegistrationRequest;
import com.iss.renterscore.authentication.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.token.email.verification.duration}")
    private Long emailVerificationExpirationDuration;

    public Optional<Users> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Users save(Users users) {
        return userRepo.save(users);
    }

    public Boolean existsByEmail(String email) {
        Users users = userRepo.existsByEmail(email);

        return users != null;
    }

    public Users createUser(RegistrationRequest registrationRequest) {

        if (existsByEmail(registrationRequest.getEmail())) {
            throw new ResourceAlreadyInUseException("Email", "Address", registrationRequest.getEmail());
        }
        Users users = new Users();
        users.setEmail(registrationRequest.getEmail());
        users.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        users.setUserRole(UserRole.ROLE_USER);
        users.setPropertyRole(PropertyRole.ROLE_TENANT);
        users.setAccountStatus(AccountStatus.ACCOUNT_ACTIVE);
        users.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);
        users.setVerificationToken(generateNewToken());
        users.setExpiryDate(Instant.now().plusMillis(emailVerificationExpirationDuration));
        users.setCreatedUser(0L);
        users.setModifiedUser(0L);
        users.setCreatedDate(Instant.now());
        users.setModifiedDate(Instant.now());
        return users;
    }

    public UserProfile createUserProfile(RegistrationRequest registrationRequest) {
        UserProfile profile = new UserProfile();
        profile.setEmail(registrationRequest.getEmail());
        profile.setPropertyRole(PropertyRole.ROLE_TENANT);
        profile.setFirstName(registrationRequest.getFirstName());
        profile.setLastName(registrationRequest.getLastName());
        profile.setCreatedUser(0L);
        profile.setModifiedUser(0L);
        profile.setCreatedDate(Instant.now());
        profile.setModifiedDate(Instant.now());
        return profile;
    }

    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    public void verifyExpiration(Users users) {
        if (users.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new InvalidTokenRequestException("Email Verification Token",
                    users.getVerificationToken(), "Expired token. Please request to send verification email.");
        }
    }

    public boolean verifyTokenExpiration(Users users) {
        return users.getExpiryDate().compareTo(Instant.now()) < 0;
    }

    public Users updateExistingTokenWithNameAndExpiry(Users users) {
        users.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);
        users.setExpiryDate(Instant.now().plusMillis(emailVerificationExpirationDuration));
        return save(users);
    }
    public Optional<Users> findByEmailToken(String token) {
        return userRepo.findByEmailToken(token);
    }

    public Optional<Users> resendEmailVerification(String email) {
        Users users = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email", "Email not found", email));
        if (users != null) {
            users.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);
            users.setVerificationToken(generateNewToken());
            users.setExpiryDate(Instant.now().plusMillis(emailVerificationExpirationDuration));
        }
        return Optional.ofNullable(users);
    }
}
