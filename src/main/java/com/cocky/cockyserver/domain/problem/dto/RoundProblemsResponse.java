package com.cocky.cockyserver.domain.problem.dto;

import java.util.List;

public record RoundProblemsResponse(Long roundId, List<ProblemSummaryResponse> problems) {
}
