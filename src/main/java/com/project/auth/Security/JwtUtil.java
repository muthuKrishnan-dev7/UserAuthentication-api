
package com.project.auth.Security;

import com.project.auth.User.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private final Set<String> blacklistedTokens =
            ConcurrentHashMap.newKeySet();

    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            validateToken(token);

            return !blacklistedTokens.contains(token);

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public void invalidateToken(String token) {
        try {
            validateToken(token);
            blacklistedTokens.add(token);

        } catch (ExpiredJwtException ex) {
            // Already expired; logout can still succeed.

        } catch (JwtException | IllegalArgumentException ex) {
            // Invalid token; nothing to invalidate.
        }
    }
}
