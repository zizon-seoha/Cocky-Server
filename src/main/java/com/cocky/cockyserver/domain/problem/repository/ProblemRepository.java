package com.cocky.cockyserver.domain.problem.repository;

import com.cocky.cockyserver.domain.problem.entity.Problem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    List<Problem> findByRoundIdOrderByIdAsc(Long roundId);
}
