package com.cocky.cockyserver.domain.submission.exception;

/** 문제가 속한 회차가 비활성이거나 마감 시각이 지난 경우(403)를 나타낸다. */
public class RoundClosedException extends RuntimeException {

    public RoundClosedException(String message) {
        super(message);
    }
}
