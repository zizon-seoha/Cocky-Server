package com.cocky.cockyserver.domain.submission.controller;

import com.cocky.cockyserver.domain.submission.dto.SubmissionRequest;
import com.cocky.cockyserver.domain.submission.dto.SubmissionResponse;
import com.cocky.cockyserver.domain.submission.service.SubmissionService;
import com.cocky.cockyserver.global.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(@AuthenticationPrincipal UserPrincipal principal,
                                                      @Valid @RequestBody SubmissionRequest request) {
        return ResponseEntity.ok(submissionService.submit(principal.userId(), request));
    }
}
