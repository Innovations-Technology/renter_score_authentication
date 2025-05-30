package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.exceptions.UnauthorizedException;
import com.iss.renterscore.authentication.exceptions.UpdatePasswordException;
import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.UserDetailsDto;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.LogoutRequest;
import com.iss.renterscore.authentication.payloads.UpdateRequest;
import com.iss.renterscore.authentication.service.CurrentUser;
import com.iss.renterscore.authentication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "https://renterscore.live", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MASTER')")
    public ResponseEntity<UserDetailsDto> getUserProfile(@CurrentUser CustomUserDetails currentUser) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");
        logger.info("Inside secured resource with user");
        Users users = userService.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User email", currentUser.getEmail(), "Not found!"));

        UserDetailsDto userDetailsDto = new UserDetailsDto(users);

        return ResponseEntity.ok(userDetailsDto);
    }

    @PostMapping("/update_profile")
    public ResponseEntity<ApiResponse> updateProfile(@CurrentUser CustomUserDetails currentUser, @RequestPart("user") UpdateRequest request, @RequestPart(value = "file", required = false) MultipartFile file) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");
        ApiResponse response;
        try {
            response = userService.updateProfile(currentUser, request, file)
                    .orElseThrow(() -> new UpdatePasswordException("---Empty---", "No such user present"));
        } catch (IOException e) {
            throw new ResourceNotFoundException("User email", currentUser.getEmail(), "Not found!" );
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser(@CurrentUser CustomUserDetails currentUser, @Valid @RequestBody LogoutRequest logoutRequest) {
        if (currentUser == null) throw new UnauthorizedException("User is not authorized!");
        userService.logoutUser(currentUser, logoutRequest);
        return ResponseEntity.ok(new ApiResponse("Log out successful", true));
    }
}
