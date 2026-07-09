package com.cocky.cockyserver.domain.submission.dto;

import com.cocky.cockyserver.domain.submission.entity.Submission;
import com.cocky.cockyserver.domain.submission.entity.Verdict;
import com.cocky.cockyserver.domain.submission.judge.JudgeResult;
import java.math.BigDecimal;

public record SubmissionResponse(
        Long submissionId,
        Verdict verdict,
        BigDecimal score,
        int passedCount,
        int totalCount,
        FeedbackResponse feedback
) {

    public static SubmissionResponse of(Submission submission, JudgeResult judgeResult) {
        return new SubmissionResponse(
                submission.getId(), submission.getVerdict(), submission.getScore(),
                judgeResult.passedCount(), judgeResult.totalCount(), FeedbackResponse.from(submission));
    }

    public record FeedbackResponse(
            BigDecimal timeScore,
            BigDecimal readabilityScore,
            BigDecimal originalityScore,
            String comment
    ) {

        /** 세 점수 컬럼이 하나라도 null이면 피드백 미제공으로 보고 객체 자체를 null로 접는다. */
        private static FeedbackResponse from(Submission submission) {
            if (submission.getTimeScore() == null || submission.getReadabilityScore() == null
                    || submission.getOriginalityScore() == null) {
                return null;
            }
            return new FeedbackResponse(submission.getTimeScore(), submission.getReadabilityScore(),
                    submission.getOriginalityScore(), submission.getFeedbackComment());
        }
    }
}
