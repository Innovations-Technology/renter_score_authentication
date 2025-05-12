package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.EmailVerificationStatus;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.repos.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.iss.renterscore.authentication.MockUserFactory.createUser;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class CustomUserDetailServiceTest {
    private UserRepo userRepo;
    private CustomUserDetailService service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        service = new CustomUserDetailService(userRepo);
    }

     @Test
     void testLoadUserByUsername_found() {
         Users mockUser = createUser(100L, "user@example.com", "securePassword");
         when(userRepo.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

         UserDetails details = service.loadUserByUsername(mockUser.getEmail());

         assertThat(details.getUsername()).isEqualTo("user@example.com");
         assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
         assertTrue(details.isEnabled());
     }

    @Test
    void testLoadUserByUsername_notFound() {
        String email = "notfound@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(email));
    }

     @Test
     void testLoadUserById_found() {
         Users mockUser = createUser(100L, "john@example.com", "secret");
         mockUser.setEmailVerificationStatus(EmailVerificationStatus.STATUS_VERIFIED);

         when(userRepo.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

         UserDetails details = service.loadUserById(mockUser.getId());

         assertThat(details).isInstanceOf(CustomUserDetails.class);
         assertTrue(details.isEnabled());
         assertEquals("john@example.com", details.getUsername());
         assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

         verify(userRepo, times(1)).findById(mockUser.getId());
     }

    @Test
    void testLoadUserById_notFound() {
        Long userId = 2L;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserById(userId));
    }
}
