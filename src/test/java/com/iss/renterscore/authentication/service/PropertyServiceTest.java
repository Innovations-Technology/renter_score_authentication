package com.iss.renterscore.authentication.service;


import com.iss.renterscore.authentication.MockUserFactory;
import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.model.*;
import com.iss.renterscore.authentication.payloads.ApiResponse;
import com.iss.renterscore.authentication.payloads.PropertyRequest;
import com.iss.renterscore.authentication.payloads.RentRequest;
import com.iss.renterscore.authentication.repos.BookmarkRepo;
import com.iss.renterscore.authentication.repos.PropertyRepo;
import com.iss.renterscore.authentication.repos.RentPropertyRepo;
import com.iss.renterscore.authentication.repos.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class PropertyServiceTest {


    @Mock
    private PropertyRepo propertyRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private FileService fileService;

    @Mock
    private BookmarkRepo bookmarkRepo;

    @Mock
    private RentPropertyRepo rentPropertyRepo;

    @InjectMocks
    private PropertyService propertyService;

    private CustomUserDetails userDetails;
    private Users user;
    private Users tenant;
    private Property property;
    private PropertyRequest propertyRequest;
    private RentRequest rentRequest;

    @BeforeEach
    void setUp() {
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setContactNumber("123456789");
        userProfile.setCompany("Test Company");

        UserProfile tenantProfile = new UserProfile();
        tenantProfile.setFirstName("Stone");
        tenantProfile.setLastName("Doe");
        tenantProfile.setContactNumber("123456789");
        tenantProfile.setCompany("Test Company");

        user = MockUserFactory.createDefaultUser();
        tenant = MockUserFactory.createUser(101L, "testing1@gmail.com", "123456");
        user.setProfile(userProfile);
        user.setUserRole(UserRole.ROLE_USER);
        user.setPropertyRole(PropertyRole.ROLE_LANDLORD);

        tenant.setProfile(tenantProfile);
        tenant.setUserRole(UserRole.ROLE_USER);
        tenant.setPropertyRole(PropertyRole.ROLE_TENANT);

        userDetails = new CustomUserDetails(user);
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
    }

    @Test
    void getAllProperties_WithUser_ReturnsPropertiesWithBookmarkStatus() {
        // Arrange
        Property property2 = new Property();
        property2.setId(2L);
        property2.setUser(user); // Set the user for property2 as well
        List<Property> properties = Arrays.asList(property, property2);

        Bookmark bookmark = new Bookmark();
        bookmark.setUsers(user);
        bookmark.setProperty(property);

        when(propertyRepo.findAllOrderByModifiedDate()).thenReturn(properties);
        // Fix: Use findByEmail instead of existsByEmail
        when(userRepo.existsByEmail(userDetails.getUsername())).thenReturn(user);
        when(bookmarkRepo.findAllByUser(user)).thenReturn(List.of(bookmark));

        // Act
        List<PropertyDto> result = propertyService.getAllProperties(userDetails);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(0).isBookmarked());
        assertFalse(result.get(1).isBookmarked());
        assertEquals("John", result.get(0).user().firstName()); // Verify profile data
    }

    @Test
    void getAllProperties_WithoutUser_ReturnsPropertiesWithoutBookmarkStatus() {
        // Arrange
        List<Property> properties = Collections.singletonList(property);
        when(propertyRepo.findAllOrderByModifiedDate()).thenReturn(properties);

        // Act
        List<PropertyDto> result = propertyService.getAllProperties(null);

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.get(0).isBookmarked());
    }

    @Test
    void getAllCreatedProperties_ReturnsUserProperties() {
        // Arrange
        List<Property> properties = Collections.singletonList(property);
        when(userRepo.existsByEmail(userDetails.getUsername())).thenReturn(user);
        when(propertyRepo.findAllByUser(user)).thenReturn(properties);

        // Act
        Optional<List<PropertyDto>> result = propertyService.getAllCreatedProperties(userDetails);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals("Test Property", result.get().get(0).title());
    }

    @Test
    void getAllBookmarkedProperties_WithBookmarks_ReturnsBookmarkedProperties() {
        // Arrange
        Bookmark bookmark = new Bookmark();
        bookmark.setUsers(user);
        bookmark.setProperty(property);

        when(userRepo.existsByEmail(userDetails.getUsername())).thenReturn(user);
        when(bookmarkRepo.findAllByUser(user)).thenReturn(List.of(bookmark));

        // Act
        Optional<List<PropertyDto>> result = propertyService.getAllBookmarkedProperties(userDetails);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertTrue(result.get().get(0).isBookmarked());
    }

    @Test
    void getAllBookmarkedProperties_NoBookmarks_ReturnsEmpty() {
        // Arrange
        when(userRepo.existsByEmail(userDetails.getUsername())).thenReturn(user);
        when(bookmarkRepo.findAllByUser(user)).thenReturn(Collections.emptyList());

        // Act
        Optional<List<PropertyDto>> result = propertyService.getAllBookmarkedProperties(userDetails);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getPropertyDetails_WithBookmark_ReturnsPropertyWithBookmarkStatus() {
        // Arrange
        when(propertyRepo.getReferenceById(1L)).thenReturn(property);
        when(userRepo.existsByEmail(userDetails.getUsername())).thenReturn(user);
        when(bookmarkRepo.findByUserIdAndPropertyId(user, property)).thenReturn(new Bookmark());

        // Act
        Optional<PropertyDto> result = propertyService.getPropertyDetails(userDetails, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().isBookmarked());
    }

    @Test
    void getPropertyDetails_WithoutBookmark_ReturnsPropertyWithoutBookmarkStatus() {
        // Arrange
        when(propertyRepo.getReferenceById(1L)).thenReturn(property);

        // Act
        Optional<PropertyDto> result = propertyService.getPropertyDetails(null, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().isBookmarked());
    }

    @Test
    void getUpdatePropertyDetails_ReturnsProperty() {
        // Arrange
        when(propertyRepo.findById(1L)).thenReturn(Optional.of(property));

        // Act
        Optional<Property> result = propertyService.getUpdatePropertyDetails(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Property", result.get().getTitle());
    }

    @Test
    void createProperty_Success_ReturnsSuccessResponse() throws IOException {
        // Arrange
        when(userRepo.existsByEmail(userDetails.getUsername())).thenReturn(user);
        when(propertyRepo.findByAddress_UnitNoAndAddress_BlockNoAndAddress_PostalCode(
                anyString(), anyString(), anyString())).thenReturn(null);
        when(fileService.saveImageFiles(any(), any(), any())).thenReturn("image_url");
        when(propertyRepo.save(any())).thenReturn(property);

        // Act
        Optional<ApiResponse> result = propertyService.createProperty(
                userDetails, propertyRequest, Collections.singletonList(mock(MultipartFile.class)));

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getSuccess());
        assertEquals("Property created successfully.", result.get().getData());
    }

    @Test
    void createProperty_ExistingProperty_ThrowsException() {
        // Arrange
        when(propertyRepo.findByAddress_UnitNoAndAddress_BlockNoAndAddress_PostalCode(
                anyString(), anyString(), anyString())).thenReturn(property);

        // Act & Assert
        assertThrows(ResourceAlreadyInUseException.class, () ->
                propertyService.createProperty(userDetails, propertyRequest, null));
    }

    @Test
    void updateProperty_Success_ReturnsSuccessResponse() throws IOException {
        // Arrange
        when(propertyRepo.getReferenceById(1L)).thenReturn(property);
        when(fileService.saveImageFiles(any(), any(), any())).thenReturn("image_url");
        when(propertyRepo.save(any())).thenReturn(property);

        // Act
        Optional<ApiResponse> result = propertyService.updateProperty(
                userDetails, 1L, propertyRequest, Collections.singletonList(mock(MultipartFile.class)));

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getSuccess());
        assertEquals("Property updated successfully.", result.get().getData());
    }

    @Test
    void deleteProperty_Success_ReturnsSuccessResponse() {
        // Arrange
        doNothing().when(propertyRepo).deleteById(1L);

        // Act
        Optional<ApiResponse> result = propertyService.deleteProperty(1L);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getSuccess());
        assertEquals("Property deleted successfully.", result.get().getData());
    }

    @Test
    void bookmarkProperty_Success_ReturnsSuccessResponse() {
        // Arrange
        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(propertyRepo.getReferenceById(1L)).thenReturn(property);
        when(bookmarkRepo.findByUserIdAndPropertyId(user, property)).thenReturn(null);

        // Act
        Optional<ApiResponse> result = propertyService.bookmarkProperty(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getSuccess());
        assertEquals("Bookmarked successfully.", result.get().getData());
    }

    @Test
    void removeBookmark_Success_ReturnsSuccessResponse() {
        // Arrange
        Bookmark bookmark = new Bookmark();
        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(propertyRepo.getReferenceById(1L)).thenReturn(property);
        when(bookmarkRepo.findByUserIdAndPropertyId(user, property)).thenReturn(bookmark);
        doNothing().when(bookmarkRepo).delete(bookmark);

        // Act
        Optional<ApiResponse> result = propertyService.removeBookmark(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getSuccess());
        assertEquals("Removed bookmark successfully.", result.get().getData());
    }

    @Test
    void requestRentProperty_Success_ReturnsSuccessResponse() {
        // Arrange
        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(propertyRepo.getReferenceById(1L)).thenReturn(property);
        when(rentPropertyRepo.findByTenantAndPropertyAndStartDateAndEndDate(
                any(), any(), any(), any())).thenReturn(null);
        when(rentPropertyRepo.save(any())).thenReturn(new RentProperty());

        // Act
        Optional<ApiResponse> result = propertyService.requestRentProperty(userDetails, rentRequest);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getSuccess());
        assertEquals("Sent rent request successfully.", result.get().getData());
    }

    @Test
    void updateRentRequest_Confirm_Success_ReturnsSuccessResponse() {
        // Arrange
        RentProperty rentProperty = new RentProperty();
        rentProperty.setRentStatus(RentStatus.REQUESTED);

        when(rentPropertyRepo.getReferenceById(1L)).thenReturn(rentProperty);
        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(propertyRepo.getReferenceById(1L)).thenReturn(property);
        when(rentPropertyRepo.save(any())).thenReturn(rentProperty);
        when(propertyRepo.save(any())).thenReturn(property);

        rentRequest.setRentStatus(RentStatus.CONFIRMED);

        // Act
        Optional<ApiResponse> result = propertyService.updateRentRequest(userDetails, 1L, rentRequest);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getSuccess());
        assertEquals("Update rent request successfully.", result.get().getData());
    }

    @Test
    void getRequestedItems_ForTenant_ReturnsRentProperties() {
        // Arrange
        CustomUserDetails tenantDetails = new CustomUserDetails(tenant);
        RentProperty rentProperty = new RentProperty();
        rentProperty.setId(1L);
        rentProperty.setOwner(user);
        rentProperty.setTenant(tenant);
        rentProperty.setProperty(property);
        rentProperty.setRentStatus(RentStatus.REQUESTED);

        when(userRepo.getReferenceById(tenantDetails.getId())).thenReturn(tenant);
        when(rentPropertyRepo.getAllRentPropertyByTenant(tenant)).thenReturn(List.of(rentProperty));

        // Act
        Optional<List<RentPropertyDto>> result = propertyService.getRequestedItems(tenantDetails);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

}
