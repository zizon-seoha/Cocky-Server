package com.cocky.cockyserver.domain.problem.entity;

import java.math.BigDecimal;

/** 문제 난이도와 전체 테스트케이스 AC 시 부여되는 base 점수(AI 피드백 30점은 별도 가산). */
public enum Difficulty {
    EASY(30),
    NORMAL(50),
    HARD(70);

    private final int baseScore;

    Difficulty(int baseScore) {
        this.baseScore = baseScore;
    }

    public BigDecimal baseScore() {
        return BigDecimal.valueOf(baseScore).setScale(2);
    }
}
