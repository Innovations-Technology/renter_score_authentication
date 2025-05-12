package com.iss.renterscore.authentication.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {
    @Test
    void testUserProfileCreation() {
        // Create test user
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");

        UserProfile profile = getUserProfile(user);

        // Verify all fields
        assertEquals(1L, profile.getId());
        assertEquals("test@example.com", profile.getEmail());
        assertEquals("John", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals("profile.jpg", profile.getProfileImage());
        assertEquals("+1234567890", profile.getContactNumber());
        assertEquals("Acme Inc", profile.getCompany());
        assertEquals(LocalDate.of(1990, 1, 1), profile.getDob());
        assertEquals("Software developer", profile.getBiography());
        assertEquals(Gender.MALE, profile.getGender());
        assertEquals(user, profile.getUser());
        assertEquals(PropertyRole.ROLE_LANDLORD, profile.getPropertyRole());
        assertEquals("123 Main St", profile.getAddress());
    }

    private static UserProfile getUserProfile(Users user) {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setEmail("test@example.com");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setProfileImage("profile.jpg");
        profile.setContactNumber("+1234567890");
        profile.setCompany("Acme Inc");
        profile.setDob(LocalDate.of(1990, 1, 1));
        profile.setBiography("Software developer");
        profile.setGender(Gender.MALE);
        profile.setUser(user);
        profile.setPropertyRole(PropertyRole.ROLE_LANDLORD);
        profile.setAddress("123 Main St");
        return profile;
    }

    @Test
    void testEqualsAndHashCode() {
        Users user1 = new Users();
        user1.setId(1L);

        UserProfile profile1 = new UserProfile();
        profile1.setId(1L);
        profile1.setUser(user1);

        UserProfile profile2 = new UserProfile();
        profile2.setId(1L);
        profile2.setUser(user1);

        UserProfile profile3 = new UserProfile();
        profile3.setId(2L);
        profile3.setUser(user1);

        // Test equality
        assertEquals(profile1, profile2);
        assertNotEquals(profile1, profile3);

        // Test hash code
        assertEquals(profile1.hashCode(), profile2.hashCode());
        assertNotEquals(profile1.hashCode(), profile3.hashCode());
    }

    @Test
    void testToString() {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setFirstName("John");
        profile.setLastName("Doe");

        String toString = profile.toString();
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("1"));
    }

    @Test
    void testGenderEnum() {
        assertEquals("MALE", Gender.MALE.toString());
        assertEquals("FEMALE", Gender.FEMALE.toString());
        assertEquals("OTHER", Gender.OTHER.toString());
    }

    @Test
    void testPropertyRoleEnum() {
        assertEquals("ROLE_LANDLORD", PropertyRole.ROLE_LANDLORD.toString());
        assertEquals("ROLE_TENANT", PropertyRole.ROLE_TENANT.toString());
        assertEquals("ROLE_AGENT", PropertyRole.ROLE_AGENT.toString());
    }

    @Test
    void testUserAssociation() {
        Users user = new Users();
        user.setId(1L);

        UserProfile profile = new UserProfile();
        profile.setUser(user);

        assertEquals(1L, profile.getUser().getId());
    }

    @Test
    void testDateOfBirthHandling() {
        UserProfile profile = new UserProfile();
        LocalDate dob = LocalDate.of(1990, 5, 15);
        profile.setDob(dob);

        assertEquals(dob, profile.getDob());
        assertEquals(1990, profile.getDob().getYear());
        assertEquals(5, profile.getDob().getMonthValue());
        assertEquals(15, profile.getDob().getDayOfMonth());
    }

    @Test
    void testEmptyBiography() {
        UserProfile profile = new UserProfile();
        profile.setBiography("");
        assertEquals("", profile.getBiography());
    }

    @Test
    void testNullImage() {
        UserProfile profile = new UserProfile();
        profile.setProfileImage(null);
        assertNull(profile.getProfileImage());
    }
}
