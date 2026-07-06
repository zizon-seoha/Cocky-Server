package com.cocky.cockyserver.domain.auth.exception;

/** 재학생이 아니거나 졸업/자퇴 등으로 가입이 허용되지 않는 경우(403)를 나타낸다. */
public class SignupNotAllowedException extends RuntimeException {

    public SignupNotAllowedException(String message) {
        super(message);
    }
}