package com.iss.renterscore.authentication.repos;

import com.iss.renterscore.authentication.model.RefreshToken;
import com.iss.renterscore.authentication.model.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findById(@NonNull Long id);

    Optional<String> findTokenById(Long id);

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    @Query("SELECT t FROM RefreshToken t WHERE t.user = :user AND t.deviceId = :deviceId")
    RefreshToken findByUserIdAndDeviceId(@Param("user") Users user, @Param("deviceId") String deviceId);

}
