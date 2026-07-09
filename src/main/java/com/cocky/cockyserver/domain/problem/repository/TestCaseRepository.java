package com.cocky.cockyserver.domain.problem.repository;

import com.cocky.cockyserver.domain.problem.entity.TestCase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByProblemIdOrderByIdAsc(Long problemId);
}
