package com.cocky.cockyserver.domain.round.controller;

import com.cocky.cockyserver.domain.problem.dto.RoundProblemsResponse;
import com.cocky.cockyserver.domain.problem.service.ProblemService;
import com.cocky.cockyserver.domain.round.dto.CurrentRoundResponse;
import com.cocky.cockyserver.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rounds")
public class RoundController {

    private final RoundService roundService;
    private final ProblemService problemService;

    @GetMapping("/current")
    public ResponseEntity<CurrentRoundResponse> getCurrentRound() {
        return ResponseEntity.ok(roundService.getCurrentRound());
    }

    @GetMapping("/current/problems")
    public ResponseEntity<RoundProblemsResponse> getCurrentRoundProblems() {
        return ResponseEntity.ok(problemService.getCurrentRoundProblems());
    }
}
