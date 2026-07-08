package com.cocky.cockyserver.domain.submission.exception;

/**
 * 채점하려는 문제에 테스트케이스가 하나도 없는 경우(500)를 나타낸다. 사용자 입력 오류가
 * 아니라 운영자가 테스트케이스를 등록하지 않은 문제 데이터 무결성 이상이다 — 이 상태로
 * 채점을 진행하면 어떤 코드든 0/0으로 AC 처리될 수 있어 반드시 막아야 한다.
 */
public class TestCaseNotConfiguredException extends RuntimeException {

    public TestCaseNotConfiguredException(String message) {
        super(message);
    }
}
