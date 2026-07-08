package com.cocky.cockyserver.domain.submission.repository;

import com.cocky.cockyserver.domain.submission.entity.Submission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserIdAndProblemIdAndLatestTrue(Long userId, Long problemId);
}
