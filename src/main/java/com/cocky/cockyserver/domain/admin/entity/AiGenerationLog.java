package com.cocky.cockyserver.domain.admin.entity;

import com.cocky.cockyserver.domain.problem.entity.Problem;
import com.cocky.cockyserver.domain.round.entity.Round;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_generation_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiGenerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "sequence_no", nullable = false, columnDefinition = "TINYINT")
    private Integer sequenceNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GenerationStatus status;

    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false, columnDefinition = "TINYINT")
    private Integer retryCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private AiGenerationLog(Round round, Problem problem, Integer sequenceNo, GenerationStatus status,
                             String errorMessage, Integer retryCount) {
        this.round = round;
        this.problem = problem;
        this.sequenceNo = sequenceNo;
        this.status = status;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.createdAt = LocalDateTime.now();
    }

    public static AiGenerationLog success(Round round, Problem problem, Integer sequenceNo, Integer retryCount) {
        return new AiGenerationLog(round, problem, sequenceNo, GenerationStatus.SUCCESS, null, retryCount);
    }

    public static AiGenerationLog failure(Round round, Integer sequenceNo, String errorMessage, Integer retryCount) {
        return new AiGenerationLog(round, null, sequenceNo, GenerationStatus.FAILED, errorMessage, retryCount);
    }
}
