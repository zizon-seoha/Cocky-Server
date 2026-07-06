package com.cocky.cockyserver.domain.auth.exception;

/** refreshToken이 만료되어 reissue가 거부된 경우(401 TOKEN_EXPIRED)를 나타낸다. */
public class RefreshTokenExpiredException extends RuntimeException {

    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
