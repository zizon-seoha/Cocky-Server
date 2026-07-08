package com.cocky.cockyserver.domain.submission.judge;

/**
 * 채점 포트. 백엔드(SubmissionService)는 이 인터페이스에만 의존한다.
 *
 * <p>12월 데모까지는 Judge0 구현체({@code infra.judge0.Judge0Adapter})를 쓰지만, 이후 자체
 * 채점 엔진으로 교체될 예정이다(CLAUDE.md §8.5) — Judge0 관련 타입/설정이 이 인터페이스
 * 밖으로 새어나가면 안 된다.
 */
public interface JudgeService {

    JudgeResult judge(JudgeRequest request);
}
