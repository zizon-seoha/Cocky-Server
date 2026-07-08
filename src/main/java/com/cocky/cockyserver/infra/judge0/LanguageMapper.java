package com.cocky.cockyserver.infra.judge0;

import com.cocky.cockyserver.domain.problem.entity.Language;
import com.cocky.cockyserver.domain.submission.judge.JudgeExecutionException;
import java.util.Map;

/**
 * {@link Language} → Judge0 language_id 매핑. 값은 실제 Judge0 인스턴스의 GET /languages
 * 응답으로 반드시 재확인할 것 — 인스턴스/버전에 따라 ID가 달라질 수 있다.
 */
public class LanguageMapper {

    private static final Map<Language, Integer> JUDGE0_LANGUAGE_IDS = Map.of(
            Language.PYTHON, 71,
            Language.C, 50,
            Language.JAVA, 62
    );

    public int toJudge0LanguageId(Language language) {
        Integer languageId = JUDGE0_LANGUAGE_IDS.get(language);
        if (languageId == null) {
            throw new JudgeExecutionException("Judge0 언어 매핑이 없습니다: " + language);
        }
        return languageId;
    }
}
