package com.cocky.cockyserver.domain.submission.judge;

import com.cocky.cockyserver.domain.submission.entity.Verdict;

/**
 * 채점 결과. verdict는 전체 테스트케이스 AC면 AC, 아니면 첫 실패 케이스의 verdict.
 * maxTimeMs/maxMemoryKb는 엔진이 측정치를 주지 못하면 null일 수 있다.
 */
public record JudgeResult(Verdict verdict, int passedCount, int totalCount, Integer maxTimeMs, Integer maxMemoryKb) {
}
