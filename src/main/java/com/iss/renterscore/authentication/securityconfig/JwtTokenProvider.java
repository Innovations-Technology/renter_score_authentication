package com.iss.renterscore.authentication.securityconfig;

import com.iss.renterscore.authentication.exceptions.InvalidTokenRequestException;
import com.iss.renterscore.authentication.model.CustomUserDetails;
import com.iss.renterscore.authentication.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

import static com.iss.renterscore.authentication.utils.Utils.EMAIL;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.expiration}")
    private Long jwtExpirationInMs;
    @Value("${app.jwt.issuer}")
    private String issuer;
    @Value("${app.jwt.audience}")
    private String audience;

    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
                .subject(String.valueOf(userDetails.getId()))
                .claim(EMAIL, userDetails.getUsername())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(jwtExpirationInMs)))
                .signWith(signSecretKey())
                .compact();
    }

    public String generateTokenByUser(Users users) {
        return Jwts.builder()
                .subject(String.valueOf(users.getId()))
                .claim(EMAIL, users.getEmail())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(jwtExpirationInMs)))
                .signWith(signSecretKey())
                .compact();
    }

    public String getEmailFromJwt(String token) {
        return extractClaims(token, claims -> claims.get(EMAIL, String.class));
    }

    public String getUserIdFromJwt(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date getTokenExpiryFromJwt(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().
                    verifyWith(signSecretKey())
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT signature");
            throw new InvalidTokenRequestException("JWT", token, e.getMessage());
        }
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
        Claims claims = Jwts.parser()
                .verifyWith(signSecretKey())
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsFunction.apply(claims);
    }

    public Long getExpiryDuration() {
        return jwtExpirationInMs;
    }

    public SecretKey signSecretKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
}
