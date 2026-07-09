package com.cocky.cockyserver.domain.problem.controller;

import com.cocky.cockyserver.domain.problem.dto.ProblemDetailResponse;
import com.cocky.cockyserver.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping("/api/v1/problems/{problemId}")
    public ResponseEntity<ProblemDetailResponse> getProblemDetail(@PathVariable Long problemId) {
        return ResponseEntity.ok(problemService.getProblemDetail(problemId));
    }
}
