package com.cocky.cockyserver.domain.submission.dto;

import com.cocky.cockyserver.domain.submission.entity.Verdict;
import java.math.BigDecimal;

public record SubmissionResponse(
        Long submissionId,
        Verdict verdict,
        BigDecimal score,
        int passedCount,
        int totalCount
) {
}
