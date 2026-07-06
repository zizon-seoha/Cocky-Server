package com.cocky.cockyserver.domain.auth.exception;

/**
 * refreshToken이 위조/형식 오류이거나, access 토큰으로 reissue를 시도했거나,
 * DB에 저장된 값과 불일치(이미 로그아웃/재발급으로 무효화)하는 경우(401 TOKEN_INVALID)를 나타낸다.
 */
public class RefreshTokenInvalidException extends RuntimeException {

    public RefreshTokenInvalidException(String message) {
        super(message);
    }
}
