package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.MockUserFactory;
import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.exceptions.UnauthorizedException;
import com.iss.renterscore.authentication.exceptions.UpdatePasswordException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.LogoutRequest;
import com.iss.renterscore.authentication.payloads.UpdateRequest;
import com.iss.renterscore.authentication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private CustomUserDetails currentUser;
    private Users user;
    private UpdateRequest updateRequest;
    private LogoutRequest logoutRequest;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        user = MockUserFactory.createUser(1L, "test@example.com", "password123");

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setContactNumber("123456789");
        userProfile.setCompany("Test Company");
        user.setProfile(userProfile);
        user.setUserRole(UserRole.ROLE_USER);
        user.setPropertyRole(PropertyRole.ROLE_LANDLORD);

        currentUser = new CustomUserDetails(user);

        updateRequest = new UpdateRequest();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Doe");

        logoutRequest = new LogoutRequest();
        logoutRequest.setDeviceId("device123");

        mockFile = mock(MultipartFile.class);
    }

    @Test
    void getUserProfile_AuthenticatedUser_ReturnsUserDetails() {
        // Arrange
        when(userService.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<UserDetailsDto> response = userController.getUserProfile(currentUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().email());
    }

    @Test
    void getUserProfile_UnauthenticatedUser_ThrowsUnauthorizedException() {
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userController.getUserProfile(null));
    }

    @Test
    void getUserProfile_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(userService.findByEmail(currentUser.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userController.getUserProfile(currentUser));
    }

    @Test
    void updateProfile_Success_ReturnsApiResponse() throws IOException {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Profile updated successfully", true);
        when(userService.updateProfile(eq(currentUser), eq(updateRequest), any(MultipartFile.class)))
                .thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = userController.updateProfile(currentUser, updateRequest, mockFile);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updateProfile_UnauthenticatedUser_ThrowsUnauthorizedException() {
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userController.updateProfile(null, updateRequest, mockFile));
    }

    @Test
    void updateProfile_UpdateFails_ThrowsUpdatePasswordException() throws IOException {
        // Arrange
        when(userService.updateProfile(eq(currentUser), eq(updateRequest), any(MultipartFile.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UpdatePasswordException.class, () -> userController.updateProfile(currentUser, updateRequest, mockFile));
    }

    @Test
    void updateProfile_IOException_ThrowsResourceNotFoundException() throws IOException {
        // Arrange
        when(userService.updateProfile(eq(currentUser), eq(updateRequest), any(MultipartFile.class)))
                .thenThrow(new IOException());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userController.updateProfile(currentUser, updateRequest, mockFile));
    }

    @Test
    void updateProfile_NoFile_WorksCorrectly() throws IOException {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Profile updated successfully", true);
        when(userService.updateProfile(eq(currentUser), eq(updateRequest), isNull()))
                .thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = userController.updateProfile(currentUser, updateRequest, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void logoutUser_Success_ReturnsApiResponse() {
        // Arrange
        doNothing().when(userService).logoutUser(currentUser, logoutRequest);

        // Act
        ResponseEntity<ApiResponse> response = userController.logoutUser(currentUser, logoutRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Log out successful", Objects.requireNonNull(response.getBody()).getData());
        assertTrue(response.getBody().getSuccess());
    }

    @Test
    void logoutUser_UnauthenticatedUser_ThrowsUnauthorizedException() {
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userController.logoutUser(null, logoutRequest));
    }
}
