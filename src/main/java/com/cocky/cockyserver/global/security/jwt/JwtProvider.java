package com.cocky.cockyserver.global.security.jwt;

import com.cocky.cockyserver.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * Access/Refresh 토큰 발급·검증을 전담한다. DataGSM 연동 로그인 로직은 다음 단계에서
 * 이 클래스를 사용해 구현하며, 여기서는 발급/검증 틀만 제공한다.
 *
 * <p>refresh 토큰이 access 토큰 대신 API 호출에 재사용되는 것을 막기 위해 "typ" 클레임으로
 * 토큰 종류를 구분한다({@link #isAccessToken(Claims)}).
 */
@Component
public class JwtProvider {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "typ";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtProvider(JwtProperties properties) {
        this.secretKey = Keys.hmacShaKeyFor(properties.secretKey().getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = properties.accessExpiration();
        this.refreshExpirationMs = properties.refreshExpiration();
    }

    public String generateAccessToken(Long userId, Role role) {
        return buildToken(userId, TYPE_ACCESS, role, accessExpirationMs);
    }

    public String generateRefreshToken(Long userId) {
        return buildToken(userId, TYPE_REFRESH, null, refreshExpirationMs);
    }

    private String buildToken(Long userId, String type, Role role, long expirationMs) {
        Date now = new Date();
        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs));
        if (role != null) {
            builder.claim(CLAIM_ROLE, role.name());
        }
        return builder.signWith(secretKey).compact();
    }

    /**
     * 서명·만료를 검증하고 Claims를 반환한다. 만료(ExpiredJwtException)와 위조/형식 오류
     * (그 외 JwtException)를 호출부(필터)에서 구분해야 하므로 예외를 감추지 않고 그대로 던진다.
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return getUserId(parseClaims(token));
    }

    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public Role getRole(String token) {
        return getRole(parseClaims(token));
    }

    public Role getRole(Claims claims) {
        return Role.valueOf(claims.get(CLAIM_ROLE, String.class));
    }

    public boolean isAccessToken(Claims claims) {
        return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public boolean isRefreshToken(Claims claims) {
        return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
    }
}