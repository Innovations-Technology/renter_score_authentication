package com.iss.renterscore.authentication.service;

import com.iss.renterscore.authentication.exceptions.TokenRefreshException;
import com.iss.renterscore.authentication.model.RefreshToken;
import com.iss.renterscore.authentication.model.Users;
import com.iss.renterscore.authentication.repos.RefreshTokenRepo;
import com.iss.renterscore.authentication.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    @Value("${app.token.refresh.duration}")
    private Long refreshTokenDurationMs;

    public Optional<RefreshToken> findById(Long id) {
        return refreshTokenRepo.findById(id);
    }

    public Optional<String> findTokenById(Long id) {
        return refreshTokenRepo.findTokenById(id);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepo.save(refreshToken);
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepo.delete(refreshToken);
    }

    public RefreshToken createRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(Utils.generateRefreshToken());
        refreshToken.setRefreshCount(0L);
        return refreshToken;
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), "Expired token. Please issue a new request.");
        }
    }

    public void deleteById(Long id) {
        refreshTokenRepo.deleteById(id);
    }

    public void deleteByUserIdAndDeviceId(Users user, String deviceId) {
        RefreshToken refreshToken = refreshTokenRepo.findByUserIdAndDeviceId(user, deviceId);
        if (refreshToken != null) {
            refreshTokenRepo.delete(refreshToken);
        }
    }

    public Optional<RefreshToken> findExistTokenByUserIdAndDeviceId(Users user, String deviceId) {
        return refreshTokenRepo.findExistTokenByUserIdAndDeviceId(user, deviceId);
    }

    public void deleteByUserId(Users user) {
        List<RefreshToken> refreshTokens = refreshTokenRepo.findByUserId(user);
        if (!refreshTokens.isEmpty()) {
            refreshTokenRepo.delete(refreshTokens.get(0));
        }
    }

    public void increaseCount(RefreshToken refreshToken) {
        refreshToken.increaseRefreshCount();
        save(refreshToken);
    }
}
