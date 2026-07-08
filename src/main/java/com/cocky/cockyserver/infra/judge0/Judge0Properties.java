package com.cocky.cockyserver.infra.judge0;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Judge0 연동 설정. token은 self-hosted Judge0(CE)의 {@code AUTHN_TOKEN} 인증을 쓸 때만
 * 채운다 — 비어 있으면 인증 헤더 없이 호출한다.
 *
 * <p>defaultTimeLimitMs/defaultMemoryLimitKb는 problem별 제한 컬럼이 아직 없어 쓰는 전역
 * 기본값이다(problem별 제한은 후속 작업).
 */
@ConfigurationProperties(prefix = "judge0")
public record Judge0Properties(
        String url,
        String token,
        long defaultTimeLimitMs,
        int defaultMemoryLimitKb
) {
}
