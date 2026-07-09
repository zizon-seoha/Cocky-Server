package com.cocky.cockyserver.domain.problem.repository;

import com.cocky.cockyserver.domain.problem.entity.Problem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    List<Problem> findByRoundIdOrderByIdAsc(Long roundId);

    /** 최근 문제 지문을 스케줄러가 pastStatements(중복 검사용)로 쓴다. */
    List<Problem> findTop20ByOrderByCreatedAtDesc();
}
