package com.cocky.cockyserver.global.security;

/**
 * 401 응답 body의 code 값. 프론트가 TOKEN_EXPIRED일 때만 재발급(reissue)을 시도하도록 구분한다.
 */
public enum AuthErrorCode {
    TOKEN_EXPIRED,
    TOKEN_INVALID
}