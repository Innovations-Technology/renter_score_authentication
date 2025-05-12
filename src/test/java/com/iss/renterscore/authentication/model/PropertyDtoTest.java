package com.iss.renterscore.authentication.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PropertyDtoTest {
    @BeforeEach
    void setUp() {
        // Setup mock servlet context for image URL building
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("example.com");
        request.setServerPort(443);
        request.setContextPath("/api");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testPropertyDtoCreation() {
        // Create test user
        Users user = new Users();
        user.setId(1L);
        user.setEmail("owner@example.com");
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        user.setProfile(profile);

        // Create test property
        Property property = new Property();
        property.setId(1L);
        property.setTitle("Beautiful Apartment");
        property.setDescription("Spacious 3-bedroom apartment");
        property.setHeroImage("hero.jpg");
        property.setPropertyType(PropertyType.HDB);
        property.setAmenities("Pool,Gym");
        property.setBedrooms(3);
        property.setBathrooms(2);

        Address address = new Address();
        address.setStreet("123 Main St");
        property.setAddress(address);

        property.setAvailableDate("2023-12-01");
        property.setPropertyStatus(PropertyStatus.FULLY_FINISHED);
        property.setPrice(2000);
        property.setCurrency("USD");
        property.setSize("1200 sqft");
        property.setRentType(RentType.MONTHLY);
        property.setUser(user);
        property.setPropertyState(PropertyState.AVAILABLE);
        property.setCreatedDate(LocalDateTime.now());
        property.setModifiedDate(LocalDateTime.now());
        property.setCreatedUser(1L);
        property.setModifiedUser(1L);

        // Add property images
        PropertyImage img1 = new PropertyImage();
        img1.setImage("img1.jpg");
        PropertyImage img2 = new PropertyImage();
        img2.setImage("img2.jpg");
        property.setImages(List.of(img1, img2));

        // Create DTO
        PropertyDto dto = new PropertyDto(property, true);

        // Verify all fields
        assertEquals(1L, dto.id());
        assertEquals("Beautiful Apartment", dto.title());
        assertEquals("Spacious 3-bedroom apartment", dto.description());
        assertEquals("https://example.com/api/hero.jpg", dto.heroImage());
        assertEquals(2, dto.images().size());
        assertEquals("https://example.com/api/img1.jpg", dto.images().get(0));
        assertEquals("https://example.com/api/img2.jpg", dto.images().get(1));
        assertEquals(PropertyType.HDB, dto.propertyType());
        assertEquals("Pool,Gym", dto.amenities());
        assertEquals(3, dto.bedrooms());
        assertEquals(2, dto.bathrooms());
        assertEquals(address, dto.address());
        assertEquals("2023-12-01", dto.availableDate());
        assertEquals(PropertyStatus.FULLY_FINISHED, dto.propertyStatus());
        assertEquals(2000, dto.price());
        assertEquals("USD", dto.currency());
        assertEquals("1200 sqft", dto.size());
        assertEquals(RentType.MONTHLY, dto.rentType());
        assertEquals(1L, dto.user().id());
        assertEquals(PropertyState.AVAILABLE, dto.propertyState());
        assertTrue(dto.isBookmarked());
        assertNotNull(dto.createdDate());
        assertNotNull(dto.modifiedDate());
        assertEquals(1L, dto.createdUser());
        assertEquals(1L, dto.modifiedUser());
    }

    @Test
    void testImageUrlBuilding() {
        // Test hero image URL
        Property property = new Property();
        property.setHeroImage("property/hero.jpg");
        property.setImages(List.of());
        Users user = new Users();
        user.setId(1L);
        user.setEmail("owner@example.com");
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        user.setProfile(profile);
        property.setUser(user);

        PropertyDto dto = new PropertyDto(property, false);
        assertEquals("https://example.com/api/property/hero.jpg", dto.heroImage());

        // Test null image handling
        property.setHeroImage(null);
        PropertyDto dtoWithNullImage = new PropertyDto(property, false);
        assertNull(dtoWithNullImage.heroImage());
    }

    @Test
    void testEmptyImageList() {
        Property property = new Property();
        property.setImages(List.of()); // Empty list
        Users user = new Users();
        user.setId(1L);
        user.setEmail("owner@example.com");
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        user.setProfile(profile);
        property.setUser(user);

        PropertyDto dto = new PropertyDto(property, false);
        assertTrue(dto.images().isEmpty());
    }

    @Test
    void testNullImageInList() {
        PropertyImage img1 = new PropertyImage();
        img1.setImage("img1.jpg");
        PropertyImage img2 = new PropertyImage();
        img2.setImage(null); // Null image

        Property property = new Property();
        property.setImages(List.of(img1, img2));
        Users user = new Users();
        user.setId(1L);
        user.setEmail("owner@example.com");
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        user.setProfile(profile);
        property.setUser(user);

        PropertyDto dto = new PropertyDto(property, false);
        assertEquals(1, dto.images().size()); // Null image should be filtered
        assertEquals("https://example.com/api/img1.jpg", dto.images().get(0));
    }

    @Test
    void testBookmarkStatus() {
        Property property = new Property();
        Users user = new Users();
        user.setId(1L);
        user.setEmail("owner@example.com");
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        user.setProfile(profile);
        property.setUser(user);
        PropertyDto bookmarkedDto = new PropertyDto(property, true);
        assertTrue(bookmarkedDto.isBookmarked());

        PropertyDto notBookmarkedDto = new PropertyDto(property, false);
        assertFalse(notBookmarkedDto.isBookmarked());
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }
}
