package com.cocky.cockyserver.domain.problem.service;

import com.cocky.cockyserver.domain.problem.dto.ProblemDetailResponse;
import com.cocky.cockyserver.domain.problem.dto.ProblemSummaryResponse;
import com.cocky.cockyserver.domain.problem.dto.RoundProblemsResponse;
import com.cocky.cockyserver.domain.problem.entity.Problem;
import com.cocky.cockyserver.domain.problem.exception.ProblemNotFoundException;
import com.cocky.cockyserver.domain.problem.repository.ProblemRepository;
import com.cocky.cockyserver.domain.round.entity.Round;
import com.cocky.cockyserver.domain.round.service.RoundService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final RoundService roundService;

    @Transactional(readOnly = true)
    public RoundProblemsResponse getCurrentRoundProblems() {
        Round round = roundService.getCurrentActiveRound();
        List<ProblemSummaryResponse> problems = problemRepository.findByRoundIdOrderByIdAsc(round.getId()).stream()
                .map(ProblemSummaryResponse::from)
                .toList();
        return new RoundProblemsResponse(round.getId(), problems);
    }

    @Transactional(readOnly = true)
    public ProblemDetailResponse getProblemDetail(Long problemId) {
        Problem problem = problemRepository
                .findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException("존재하지 않는 문제입니다. problemId=" + problemId));
        return ProblemDetailResponse.from(problem);
    }
}
