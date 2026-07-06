package com.cocky.cockyserver.global.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정 바인딩. 값은 application.yml → 환경변수(JWT_SECRET_KEY 등)에서 온다.
 * secretKey는 절대 로그에 싣지 않는다.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secretKey,
        long accessExpiration,
        long refreshExpiration
) {
}