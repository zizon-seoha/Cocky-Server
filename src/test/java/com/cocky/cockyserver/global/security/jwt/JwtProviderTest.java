package com.cocky.cockyserver.global.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cocky.cockyserver.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private static final String SECRET = "test-only-jwt-secret-key-not-for-production-use-1234567890";

    @Test
    void generatesAndValidatesAccessToken() {
        JwtProvider jwtProvider = new JwtProvider(new JwtProperties(SECRET, 3_600_000L, 1_209_600_000L));

        String token = jwtProvider.generateAccessToken(42L, Role.STUDENT);
        Claims claims = jwtProvider.parseClaims(token);

        assertTrue(jwtProvider.isAccessToken(claims));
        assertEquals(42L, jwtProvider.getUserId(claims));
        assertEquals(Role.STUDENT, jwtProvider.getRole(claims));
    }

    @Test
    void refreshTokenIsNotTreatedAsAccessToken() {
        JwtProvider jwtProvider = new JwtProvider(new JwtProperties(SECRET, 3_600_000L, 1_209_600_000L));

        String token = jwtProvider.generateRefreshToken(7L);
        Claims claims = jwtProvider.parseClaims(token);

        assertFalse(jwtProvider.isAccessToken(claims));
        assertEquals(7L, jwtProvider.getUserId(claims));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtProvider jwtProvider = new JwtProvider(new JwtProperties(SECRET, -1_000L, 1_209_600_000L));

        String token = jwtProvider.generateAccessToken(1L, Role.ADMIN);

        assertThrows(ExpiredJwtException.class, () -> jwtProvider.parseClaims(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtProvider jwtProvider = new JwtProvider(new JwtProperties(SECRET, 3_600_000L, 1_209_600_000L));
        String token = jwtProvider.generateAccessToken(1L, Role.ADMIN);
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        assertThrows(io.jsonwebtoken.security.SignatureException.class, () -> jwtProvider.parseClaims(tampered));
    }
}