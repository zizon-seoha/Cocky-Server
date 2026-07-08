package com.cocky.cockyserver.domain.submission.exception;

/** 제출 언어가 문제에 고정된 언어와 다른 경우(400)를 나타낸다. */
public class LanguageMismatchException extends RuntimeException {

    public LanguageMismatchException(String message) {
        super(message);
    }
}
