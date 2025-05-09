package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.ResourceAlreadyInUseException;
import com.iss.renterscore.authentication.model.PropertyRole;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.payloads.RegistrationRequest;
import com.iss.renterscore.authentication.repos.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@TestPropertySource(properties = "app.token.email.verification.duration=60000")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        ReflectionTestUtils.setField(userService, "emailVerificationExpirationDuration", 60000L);

    }

    @Test
    void testFindByEmail_UserExists_ReturnsUser() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        Optional<Users> foundUser = userService.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testExistsByEmail_UserExists_ReturnsTrue() {
        when(userRepo.existsByEmail("test@example.com")).thenReturn(testUser);

        boolean exists = userService.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_UserDoesNotExist_ReturnsFalse() {
        when(userRepo.existsByEmail("unknown@example.com")).thenReturn(null);

        boolean exists = userService.existsByEmail("unknown@example.com");

        assertFalse(exists);
    }

    @Test
    void testCreateUser_EmailAlreadyExists_ThrowsException() {
        when(userRepo.existsByEmail("test@example.com")).thenReturn(testUser);

        Exception exception = assertThrows(ResourceAlreadyInUseException.class, () -> {
            userService.createUser(new RegistrationRequest("Test", "QAC", "test@example.com", "password", PropertyRole.ROLE_TENANT));
        });

        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    void testTokenExpiration() {
        assertNotNull(userService);
        assertEquals(60000L, userService.emailVerificationExpirationDuration);
    }

    @Test
    void testCreateUser_Success() {
        RegistrationRequest request = new RegistrationRequest("User", "New", "new@example.com", "password", PropertyRole.ROLE_TENANT);
        when(userRepo.existsByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        Users newUser = userService.createUser(request);

        assertEquals("new@example.com", newUser.getEmail());
        assertEquals("encodedPassword", newUser.getPassword());
    }

}
