package com.cocky.cockyserver.domain.submission.repository;

import com.cocky.cockyserver.domain.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * 같은 (user, problem)의 기존 is_latest=true 제출을 전부 false로 뒤집는다. 조회 후
     * forEach로 하나씩 바꾸는 대신 벌크 UPDATE 한 방으로 처리해 읽기-쓰기 갭의 경쟁상태를
     * 줄인다. 완전한 방어(동시 제출 직렬화)는 DB unique index로 별도 처리할 예정(백로그).
     */
    @Modifying
    @Query("update Submission s set s.latest = false "
            + "where s.user.id = :userId and s.problem.id = :problemId and s.latest = true")
    int markPreviousNotLatest(@Param("userId") Long userId, @Param("problemId") Long problemId);
}
