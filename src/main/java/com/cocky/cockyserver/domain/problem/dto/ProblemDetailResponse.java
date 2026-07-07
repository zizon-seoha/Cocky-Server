package com.cocky.cockyserver.domain.problem.dto;

import com.cocky.cockyserver.domain.problem.entity.Problem;

public record ProblemDetailResponse(Long problemId, String title, String content, String language) {

    public static ProblemDetailResponse from(Problem problem) {
        return new ProblemDetailResponse(
                problem.getId(), problem.getTitle(), problem.getContent(), problem.getLanguage().name());
    }
}
