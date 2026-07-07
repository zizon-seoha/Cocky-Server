package com.cocky.cockyserver.domain.problem.dto;

import com.cocky.cockyserver.domain.problem.entity.Problem;

public record ProblemSummaryResponse(Long problemId, String title, String language) {

    public static ProblemSummaryResponse from(Problem problem) {
        return new ProblemSummaryResponse(problem.getId(), problem.getTitle(), problem.getLanguage().name());
    }
}
