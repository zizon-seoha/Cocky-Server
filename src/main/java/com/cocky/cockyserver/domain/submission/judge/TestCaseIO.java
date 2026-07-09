package com.cocky.cockyserver.domain.submission.judge;

/** 채점 1건에 쓰이는 입력/기대출력 쌍. */
public record TestCaseIO(String input, String expectedOutput) {
}
