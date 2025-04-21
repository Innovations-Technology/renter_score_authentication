package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.TokenRefreshException;
import com.iss.renterscore.authentication.model.RefreshToken;
import com.iss.renterscore.authentication.repos.RefreshTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "app.token.refresh.duration=900000")
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        refreshToken = new RefreshToken();
        refreshToken.setToken("sampleToken");
        refreshToken.setExpiryDate(Instant.now().plusMillis(900000)); // 15 min expiry
        refreshToken.setRefreshCount(0L);

        // Set the @Value property manually
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 900000L);
    }

    @Test
    void testFindById_TokenExists_ReturnsToken() {
        when(refreshTokenRepo.findById(1L)).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> foundToken = refreshTokenService.findById(1L);

        assertTrue(foundToken.isPresent());
        assertEquals("sampleToken", foundToken.get().getToken());
    }

    @Test
    void testFindById_TokenDoesNotExist_ReturnsEmpty() {
        when(refreshTokenRepo.findById(2L)).thenReturn(Optional.empty());

        Optional<RefreshToken> foundToken = refreshTokenService.findById(2L);

        assertFalse(foundToken.isPresent());
    }

    @Test
    void testFindByToken_TokenExists_ReturnsToken() {
        when(refreshTokenRepo.findByToken("sampleToken")).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken("sampleToken");

        assertTrue(foundToken.isPresent());
        assertEquals("sampleToken", foundToken.get().getToken());
    }

    @Test
    void testFindByToken_TokenDoesNotExist_ReturnsEmpty() {
        when(refreshTokenRepo.findByToken("unknownToken")).thenReturn(Optional.empty());

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken("unknownToken");

        assertFalse(foundToken.isPresent());
    }

    @Test
    void testCreateRefreshToken_Success() {
        RefreshToken newToken = refreshTokenService.createRefreshToken();

        assertNotNull(newToken);
        assertNotNull(newToken.getToken());
        assertEquals(0L, newToken.getRefreshCount());
        assertTrue(newToken.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    void testVerifyExpiration_ValidToken_DoesNotThrowException() {
        assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(refreshToken));
    }

    @Test
    void testVerifyExpiration_ExpiredToken_ThrowsException() {
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setToken("expiredToken");
        expiredToken.setExpiryDate(Instant.now().minusMillis(1000));

        Exception exception = assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });

        assertTrue(exception.getMessage().contains("Expired token"));
    }

    @Test
    void testIncreaseCount_Success() {
        refreshTokenService.increaseCount(refreshToken);

        assertEquals(1L, refreshToken.getRefreshCount());
        verify(refreshTokenRepo, times(1)).save(refreshToken);
    }

    @Test
    void testDeleteById_Success() {
        refreshTokenService.deleteById(1L);

        verify(refreshTokenRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteToken_Success() {
        refreshTokenService.delete(refreshToken);

        verify(refreshTokenRepo, times(1)).delete(refreshToken);
    }
}
