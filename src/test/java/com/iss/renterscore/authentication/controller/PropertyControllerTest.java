package com.iss.renterscore.authentication.controller;

import com.iss.renterscore.authentication.MockUserFactory;
import com.iss.renterscore.authentication.exceptions.AppException;
import com.iss.renterscore.authentication.exceptions.DataAlreadyExistException;
import com.iss.renterscore.authentication.exceptions.ResourceNotFoundException;
import com.iss.renterscore.authentication.exceptions.UnauthorizedException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.PropertyRequest;
import com.iss.renterscore.authentication.payloads.RentRequest;
import com.iss.renterscore.authentication.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private PropertyController propertyController;

    private CustomUserDetails currentUser;
    private PropertyRequest propertyRequest;
    private RentRequest rentRequest;
    private MultipartFile mockFile;
    private PropertyDto propertyDto;
    private Users tenant;
    private Users user;
    Property property;


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

        tenant = MockUserFactory.createUser(101L, "testing1@gmail.com", "123456");
        UserProfile tenantProfile = new UserProfile();
        tenantProfile.setFirstName("Stone");
        tenantProfile.setLastName("Doe");
        tenantProfile.setContactNumber("123456789");
        tenantProfile.setCompany("Test Company");

        tenant.setProfile(tenantProfile);
        tenant.setUserRole(UserRole.ROLE_USER);
        tenant.setPropertyRole(PropertyRole.ROLE_TENANT);

        currentUser = new CustomUserDetails(user);

        propertyRequest = new PropertyRequest();
        propertyRequest.setTitle("Test Property");
        propertyRequest.setUnitNo("101");
        propertyRequest.setBlockNo("1");
        propertyRequest.setPostalCode("123456");

        rentRequest = new RentRequest();
        rentRequest.setPropertyId(1L);
        rentRequest.setStartDate("01/01/2023");
        rentRequest.setEndDate("31/12/2023");
        rentRequest.setRentStatus(RentStatus.REQUESTED);

        mockFile = mock(MultipartFile.class);
        Address address = new Address("1", "101", "Test Street", "123456", Regions.CENTRAL_SINGAPORE);

        property = new Property();
        property.setId(1L);
        property.setTitle("Test Property");
        property.setUser(user);
        property.setAddress(address);
        property.setCreatedDate(LocalDateTime.now());
        property.setModifiedDate(LocalDateTime.now());
        property.setCreatedUser(1L);
        property.setModifiedUser(1L);
        property.setImages(new ArrayList<>());

        propertyDto = new PropertyDto(property, false);
    }

    @Test
    void getAllProperties_WithUser_ReturnsProperties() {
        // Arrange
        List<PropertyDto> properties = Collections.singletonList(propertyDto);
        when(propertyService.getAllProperties(currentUser)).thenReturn(properties);

        // Act
        ResponseEntity<List<PropertyDto>> response = propertyController.getAllProperties(currentUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Test Property", response.getBody().get(0).title());
    }

    @Test
    void getPropertyDetails_Success_ReturnsProperty() {
        // Arrange
        when(propertyService.getPropertyDetails(currentUser, 1L)).thenReturn(Optional.of(propertyDto));

        // Act
        ResponseEntity<PropertyDto> response = propertyController.getPropertyDetails(currentUser, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Property", Objects.requireNonNull(response.getBody()).title());
    }

    @Test
    void getPropertyDetails_NotFound_ThrowsException() {
        // Arrange
        when(propertyService.getPropertyDetails(currentUser, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> propertyController.getPropertyDetails(currentUser, 1L));
    }

    @Test
    void getUpdatePropertyDetails_Unauthorized_ThrowsException() {
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> propertyController.getUpdatePropertyDetails(null, 1L));
    }

    @Test
    void createProperty_Success_ReturnsApiResponse() throws IOException {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Property created successfully", true);
        when(propertyService.createProperty(eq(currentUser), any(PropertyRequest.class), anyList()))
                .thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = propertyController.createProperty(
                currentUser, propertyRequest, Collections.singletonList(mockFile));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void createProperty_ExistingProperty_ThrowsException() throws IOException {
        when(propertyService.createProperty(any(), any(), any()))
                .thenThrow(new DataAlreadyExistException("Property exists", "Duplicate property"));

        Executable createPropertyExecutable = () ->
                propertyController.createProperty(currentUser, propertyRequest, Collections.singletonList(mockFile));

        assertThrows(DataAlreadyExistException.class, createPropertyExecutable);
    }

    @Test
    void createProperty_IOException_ThrowsAppException() throws IOException {
        // Arrange
        when(propertyService.createProperty(eq(currentUser), any(PropertyRequest.class), anyList()))
                .thenThrow(new IOException());

        Executable createPropertyExecutable = () ->
                propertyController.createProperty(currentUser, propertyRequest, Collections.singletonList(mockFile));

        assertThrows(AppException.class, createPropertyExecutable);
    }

    @Test
    void updateProperty_Success_ReturnsApiResponse() throws IOException {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Property updated successfully", true);
        when(propertyService.updateProperty(eq(currentUser), anyLong(), any(PropertyRequest.class), anyList()))
                .thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = propertyController.updateProperty(
                currentUser, 1L, propertyRequest, Collections.singletonList(mockFile));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void deleteProperty_Success_ReturnsApiResponse() {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Property deleted successfully", true);
        when(propertyService.deleteProperty(1L)).thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = propertyController.deleteProperty(currentUser, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getAllBookmarkedProperties_Success_ReturnsProperties() {
        // Arrange
        List<PropertyDto> properties = Collections.singletonList(propertyDto);
        when(propertyService.getAllBookmarkedProperties(currentUser)).thenReturn(Optional.of(properties));

        // Act
        ResponseEntity<List<PropertyDto>> response = propertyController.getAllBookmarkedProperties(currentUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Test Property", response.getBody().get(0).title());
    }

    @Test
    void bookmarkProperty_Success_ReturnsApiResponse() {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Bookmarked successfully", true);
        when(propertyService.bookmarkProperty(1L, 1L)).thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = propertyController.bookmarkProperty(currentUser, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void unBookmarkProperty_Success_ReturnsApiResponse() {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Bookmark removed successfully", true);
        when(propertyService.removeBookmark(1L, 1L)).thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = propertyController.unBookmarkProperty(currentUser, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void requestRentProperty_Success_ReturnsApiResponse() {
        // Arrange
        ApiResponse expectedResponse = new ApiResponse("Rent request sent successfully", true);
        when(propertyService.requestRentProperty(currentUser, rentRequest)).thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<ApiResponse> response = propertyController.requestRentProperty(currentUser, rentRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getRequestedItems_Success_ReturnsRentProperties() {
        // Arrange
        currentUser = new CustomUserDetails(tenant);
        RentProperty rentProperty = new RentProperty();
        rentProperty.setId(1L);
        rentProperty.setOwner(user);
        rentProperty.setTenant(tenant);
        rentProperty.setProperty(property);
        rentProperty.setRentStatus(RentStatus.REQUESTED);
        RentPropertyDto rentPropertyDto = new RentPropertyDto(rentProperty);
        when(propertyService.getRequestedItems(currentUser)).thenReturn(Optional.of(List.of(rentPropertyDto)));

        // Act
        ResponseEntity<List<RentPropertyDto>> response = propertyController.getRequestedItems(currentUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }
}
