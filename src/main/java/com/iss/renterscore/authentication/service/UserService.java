package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.InvalidTokenRequestException;
import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.exceptions.UserLogoutException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.LogoutRequest;
import com.iss.renterscore.authentication.payloads.RegistrationRequest;
import com.iss.renterscore.authentication.payloads.UpdateRequest;
import com.iss.renterscore.authentication.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
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

        if (Boolean.TRUE.equals(existsByEmail(registrationRequest.getEmail()))) {
            throw new ResourceAlreadyInUseException("Email", "Address", registrationRequest.getEmail());
        }
        Users users = new Users();
        users.setEmail(registrationRequest.getEmail());
        users.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        users.setUserRole(UserRole.ROLE_USER);
        users.setPropertyRole(registrationRequest.getRole());
        users.setAccountStatus(AccountStatus.ACCOUNT_ACTIVE);
        users.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);
        users.setVerificationToken(generateNewToken());
        users.setExpiryDate(Instant.now().plusMillis(emailVerificationExpirationDuration));
        return users;
    }

    public UserProfile createUserProfile(RegistrationRequest registrationRequest) {
        UserProfile profile = new UserProfile();
        profile.setEmail(registrationRequest.getEmail());
        profile.setPropertyRole(PropertyRole.ROLE_TENANT);
        profile.setFirstName(registrationRequest.getFirstName());
        profile.setLastName(registrationRequest.getLastName());
        profile.setGender(Gender.OTHER);
        return profile;
    }

    public String generateNewToken() {
        StringBuilder token = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(index));
        }
        return token.toString();
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

    public Users updateNewTokenWithExpiry(Users users) {
        users.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);
        users.setVerificationToken(generateNewToken());
        users.setExpiryDate(Instant.now().plusMillis(emailVerificationExpirationDuration));
        return save(users);
    }

    public Optional<ApiResponse> updateProfile(CustomUserDetails customUserDetails, UpdateRequest request, MultipartFile file) {
        ApiResponse response = new ApiResponse("Profile updated successfully.", true);

        Users currentUsers = findByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(customUserDetails.getEmail(), "Email", "No matching user found"));

        /*String fileName = "";
        fileName = request.getProfileImage();
        if (file != null && !file.isEmpty()) {
            fileName = fileStorageService.storeFile(file, uploadDir);
            logger.info("Profile file name: " + fileName);
        }else {
            if(fileStorageService.fileExists(currentUsers.getImageUrl(), uploadDir)) {
                fileName = currentUsers.getImageUrl();
            }
        }*/
        currentUsers.setPropertyRole(request.getPropertyRole());
        UserProfile profile = currentUsers.getProfile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDob(convertDate(request.getDob()));
        profile.setGender(request.getGender());
        profile.setContactNumber(request.getContactNumber());
        profile.setCompany(request.getCompany());
        profile.setBiography(request.getBiography());
        profile.setAddress(request.getAddress());
        profile.setProfileImage(request.getProfileImage());
        currentUsers.setProfile(profile);
        try {
            save(currentUsers);
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new ApiResponse("Update profile failed with "+ e.getMessage(), false);
        }
        return Optional.of(response);
    }

    public void logoutUser(CustomUserDetails customUserDetails, LogoutRequest logoutRequest) {
        String deviceId = logoutRequest.getDeviceId();
        RefreshToken token = refreshTokenService.findExistTokenByUserIdAndDeviceId(customUserDetails, deviceId)
                .orElseThrow(() -> new UserLogoutException(logoutRequest.getDeviceId(), "Invalid device Id supplied. No matching device found for the given user"));
        refreshTokenService.deleteById(token.getId());
        refreshTokenService.delete(token);
        refreshTokenService.deleteByUserId(customUserDetails);
    }

    private LocalDate convertDate(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        return LocalDate.parse(dob, formatter);
    }

}
