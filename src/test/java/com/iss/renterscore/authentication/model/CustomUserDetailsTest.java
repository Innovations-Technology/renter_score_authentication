package com.iss.renterscore.authentication.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {
    @Test
    void testUserDetailsCreation() {
        // Arrange
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("securePassword123");
        user.setUserRole(UserRole.ROLE_ADMIN);
        user.setEmailVerificationStatus(EmailVerificationStatus.STATUS_VERIFIED);

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Assert
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("securePassword123", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void testGetAuthorities() {
        // Arrange
        Users user = new Users();
        user.setUserRole(UserRole.ROLE_USER);

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }

    @Test
    void testAccountStatusChecks() {
        // Arrange - Verified user
        Users verifiedUser = new Users();
        verifiedUser.setUserRole(UserRole.ROLE_USER);
        verifiedUser.setEmailVerificationStatus(EmailVerificationStatus.STATUS_VERIFIED);

        // Arrange - Unverified user
        Users unverifiedUser = new Users();
        unverifiedUser.setUserRole(UserRole.ROLE_USER);
        unverifiedUser.setEmailVerificationStatus(EmailVerificationStatus.STATUS_PENDING);

        // Act & Assert
        assertTrue(new CustomUserDetails(verifiedUser).isEnabled());
        assertFalse(new CustomUserDetails(unverifiedUser).isEnabled());
        assertTrue(new CustomUserDetails(verifiedUser).isAccountNonExpired());
        assertTrue(new CustomUserDetails(verifiedUser).isCredentialsNonExpired());
    }

    @Test
    void testInheritedUserProperties() {
        // Arrange
        Users user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUserRole(UserRole.ROLE_USER);

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Assert
        assertEquals(1L, userDetails.getId());
        assertEquals("test@example.com", userDetails.getEmail());
    }

    @Test
    void testIsAccountNonLocked() {
        // Arrange
        Users user = new Users();

        user.setUserRole(UserRole.ROLE_USER);
        // Act & Assert
        // Default implementation returns true
        assertTrue(new CustomUserDetails(user).isAccountNonLocked());
    }

    @Test
    void testDifferentUserRoles() {
        // Test all enum values to ensure proper authority conversion
        for (UserRole role : UserRole.values()) {
            Users user = new Users();
            user.setUserRole(role);

            CustomUserDetails userDetails = new CustomUserDetails(user);
            GrantedAuthority authority = userDetails.getAuthorities().iterator().next();

            assertEquals(role.name(), authority.getAuthority());
        }
    }

    @Test
    void testEmailVerificationStatusImpact() {
        // Test all email verification statuses
        for (EmailVerificationStatus status : EmailVerificationStatus.values()) {
            Users user = new Users();
            user.setEmailVerificationStatus(status);
            user.setUserRole(UserRole.ROLE_USER);

            boolean expectedEnabled = status == EmailVerificationStatus.STATUS_VERIFIED;
            assertEquals(expectedEnabled, new CustomUserDetails(user).isEnabled());
        }
    }

}
