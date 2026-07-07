package com.cocky.cockyserver.domain.problem.exception;

/** 존재하지 않는 problemId로 조회한 경우(404)를 나타낸다. */
public class ProblemNotFoundException extends RuntimeException {

    public ProblemNotFoundException(String message) {
        super(message);
    }
}
