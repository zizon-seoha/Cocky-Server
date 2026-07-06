package com.cocky.cockyserver.domain.auth.exception;

/** DataGSM 인증 서버 장애/타임아웃 등 우리 쪽 입력 문제가 아닌 실패(502)를 나타낸다. */
public class OAuthServerException extends RuntimeException {

    public OAuthServerException(String message) {
        super(message);
    }
}
