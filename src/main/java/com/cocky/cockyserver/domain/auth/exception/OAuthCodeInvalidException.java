package com.cocky.cockyserver.domain.auth.exception;

/** DataGSM 인가 코드 교환/조회 실패(401)를 나타낸다. */
public class OAuthCodeInvalidException extends RuntimeException {

    public OAuthCodeInvalidException(String message) {
        super(message);
    }
}