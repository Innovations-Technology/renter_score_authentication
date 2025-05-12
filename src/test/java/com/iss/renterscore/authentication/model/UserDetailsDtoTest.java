package com.iss.renterscore.authentication.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsDtoTest {

    @BeforeEach
    void setup() {
        // Mock the servlet context for image URL building
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("/api");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testConstructorWithUser() {
        // Create test data
        LocalDateTime now = LocalDateTime.now();
        LocalDate dob = LocalDate.of(1990, 5, 15);

        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUserRole(UserRole.ROLE_USER);
        user.setPropertyRole(PropertyRole.ROLE_TENANT);
        user.setEmailVerificationStatus(EmailVerificationStatus.STATUS_VERIFIED);
        user.setAccountStatus(AccountStatus.ACCOUNT_ACTIVE);
        user.setCreatedDate(now);
        user.setCreatedUser(1L);

        UserProfile profile = getUserProfile(dob, now);

        user.setProfile(profile);

        // Create DTO
        UserDetailsDto dto = new UserDetailsDto(user);

        // Verify all fields
        assertEquals(1L, dto.id());
        assertEquals("test@example.com", dto.email());
        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
        assertNotNull(dto.profileImage()); // Should contain full URL
        assertEquals("+1234567890", dto.contactNumber());
        assertEquals("Acme Inc", dto.company());
        assertEquals(UserRole.ROLE_USER, dto.userRole());
        assertEquals(PropertyRole.ROLE_TENANT, dto.propertyRole());
        assertEquals(EmailVerificationStatus.STATUS_VERIFIED, dto.emailVerificationStatus());
        assertEquals(AccountStatus.ACCOUNT_ACTIVE, dto.accountStatus());
        assertEquals(10L, dto.profileId());
        assertEquals(dob, dto.dob());
        assertEquals("Software Developer", dto.biography());
        assertEquals(Gender.MALE, dto.gender());
        assertEquals("123 Main St", dto.address());
        assertEquals(now, dto.createdDate());
        assertEquals(now, dto.modifiedDate());
        assertEquals(1L, dto.createdUser());
        assertEquals(1L, dto.modifiedUser());
    }

    private static UserProfile getUserProfile(LocalDate dob, LocalDateTime now) {
        UserProfile profile = new UserProfile();
        profile.setId(10L);
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setProfileImage("profile.jpg");
        profile.setContactNumber("+1234567890");
        profile.setCompany("Acme Inc");
        profile.setDob(dob);
        profile.setBiography("Software Developer");
        profile.setGender(Gender.MALE);
        profile.setAddress("123 Main St");
        profile.setModifiedDate(now);
        profile.setModifiedUser(1L);
        return profile;
    }

    @Test
    void testImageUrlBuilding() {
        Users user = new Users();
        UserProfile profile = new UserProfile();
        profile.setProfileImage("images/profile.jpg");
        user.setProfile(profile);

        UserDetailsDto dto = new UserDetailsDto(user);

        assertEquals("http://localhost:8080/api/images/profile.jpg", dto.profileImage());
    }

    @Test
    void testNullImageHandling() {
        Users user = new Users();
        UserProfile profile = new UserProfile();
        profile.setProfileImage(null);
        user.setProfile(profile);

        UserDetailsDto dto = new UserDetailsDto(user);

        assertNull(dto.profileImage());
    }


    @Test
    void testDateFormats() {
        LocalDateTime now = LocalDateTime.of(2023, 6, 15, 10, 30);
        LocalDate dob = LocalDate.of(1990, 5, 15);

        Users user = new Users();
        user.setCreatedDate(now);

        UserProfile profile = new UserProfile();
        profile.setDob(dob);
        profile.setModifiedDate(now);
        user.setProfile(profile);

        UserDetailsDto dto = new UserDetailsDto(user);

        assertEquals(15, dto.dob().getDayOfMonth());
        assertEquals(5, dto.dob().getMonthValue());
        assertEquals(1990, dto.dob().getYear());

        assertEquals(2023, dto.createdDate().getYear());
        assertEquals(6, dto.createdDate().getMonthValue());
        assertEquals(10, dto.createdDate().getHour());
    }

    @Test
    void testEnumValues() {
        Users user = new Users();
        user.setUserRole(UserRole.ROLE_ADMIN);
        user.setPropertyRole(PropertyRole.ROLE_LANDLORD);
        user.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);
        user.setAccountStatus(AccountStatus.ACCOUNT_SUSPEND);

        UserProfile profile = new UserProfile();
        profile.setGender(Gender.FEMALE);
        user.setProfile(profile);

        UserDetailsDto dto = new UserDetailsDto(user);

        assertEquals(UserRole.ROLE_ADMIN, dto.userRole());
        assertEquals(PropertyRole.ROLE_LANDLORD, dto.propertyRole());
        assertEquals(EmailVerificationStatus.STATUS_PENDING, dto.emailVerificationStatus());
        assertEquals(AccountStatus.ACCOUNT_SUSPEND, dto.accountStatus());
        assertEquals(Gender.FEMALE, dto.gender());
    }

    @AfterEach
    void tearDown() {
        // Clear the request context after each test
        RequestContextHolder.resetRequestAttributes();
    }
}
