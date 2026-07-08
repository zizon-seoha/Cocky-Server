package com.cocky.cockyserver.domain.submission.judge;

import com.cocky.cockyserver.domain.problem.entity.Language;
import java.util.List;

/** 채점 요청 — 제출 코드 1건과 그 문제의 전체 테스트케이스. */
public record JudgeRequest(Language language, String code, List<TestCaseIO> cases) {
}
