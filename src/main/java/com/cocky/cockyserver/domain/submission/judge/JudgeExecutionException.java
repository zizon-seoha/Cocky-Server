package com.cocky.cockyserver.domain.submission.judge;

/** 채점 엔진 호출/응답 처리 실패(502) — 절대 성공 응답으로 삼키지 않는다. */
public class JudgeExecutionException extends RuntimeException {

    public JudgeExecutionException(String message) {
        super(message);
    }

    public JudgeExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
