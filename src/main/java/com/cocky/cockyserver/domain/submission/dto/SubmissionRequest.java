package com.cocky.cockyserver.domain.submission.dto;

import com.cocky.cockyserver.domain.problem.entity.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * isAnonymous는 요청으로 받되 저장하지 않는다 — is_anonymous 컬럼이 아직 스키마에 없음
 * (익명 닉네임 기능은 Phase 범위 밖, TODO: 스키마/기능 확정 시 반영).
 */
public record SubmissionRequest(
        @NotNull(message = "problemId는 필수입니다.") Long problemId,
        @NotNull(message = "language는 필수입니다.") Language language,
        @NotBlank(message = "code는 필수입니다.") String code,
        Boolean isAnonymous
) {
}
