package com.iss.renterscore.authentication.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

 class UserDtoTest {
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
    void testUserDtoCreation() {
        // Create test user
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPropertyRole(PropertyRole.ROLE_LANDLORD);

        // Create test profile
        UserProfile profile = new UserProfile();
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setProfileImage("images/profile.jpg");
        profile.setContactNumber("+1234567890");
        profile.setCompany("Acme Inc");
        user.setProfile(profile);

        // Create DTO
        UserDto dto = new UserDto(user);

        // Verify all fields
        assertEquals(1L, dto.id());
        assertEquals("test@example.com", dto.email());
        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
        assertEquals("https://example.com/api/images/profile.jpg", dto.profileImage());
        assertEquals("+1234567890", dto.contactNumber());
        assertEquals("Acme Inc", dto.company());
        assertEquals(PropertyRole.ROLE_LANDLORD, dto.propertyRole());
    }

    @Test
    void testImageUrlBuilding() {
        // Test with full path
        assertEquals(
                "https://example.com/api/images/avatar.jpg",
                UserDto.buildImageUrl("images/avatar.jpg")
        );

        // Test with null input
        assertNull(UserDto.buildImageUrl(null));

        // Test with empty input
        assertNull(UserDto.buildImageUrl(""));
    }

    @Test
    void testUserDtoWithNullProfileImage() {
        Users user = new Users();
        user.setId(2L);
        user.setEmail("noimage@example.com");

        UserProfile profile = new UserProfile();
        profile.setFirstName("Alice");
        profile.setProfileImage(null); // No profile image
        user.setProfile(profile);

        UserDto dto = new UserDto(user);

        assertNull(dto.profileImage());
    }

    @Test
    void testUserDtoWithEmptyProfileFields() {
        Users user = new Users();
        user.setId(3L);
        user.setEmail("empty@example.com");

        UserProfile profile = new UserProfile();
        profile.setFirstName("Bob");
        profile.setContactNumber(""); // Empty contact number
        profile.setCompany("  "); // Blank company name
        user.setProfile(profile);

        UserDto dto = new UserDto(user);

        assertEquals("", dto.contactNumber());
        assertEquals("  ", dto.company());
    }

    @Test
    void testPropertyRoleMapping() {
        Users user = new Users();
        user.setPropertyRole(PropertyRole.ROLE_TENANT);

        UserProfile profile = new UserProfile();
        profile.setFirstName("Test");
        user.setProfile(profile);

        UserDto dto = new UserDto(user);

        assertEquals(PropertyRole.ROLE_TENANT, dto.propertyRole());
    }

    @AfterEach
    void tearDown() {
        // Clean up the request context
        RequestContextHolder.resetRequestAttributes();
    }
}
