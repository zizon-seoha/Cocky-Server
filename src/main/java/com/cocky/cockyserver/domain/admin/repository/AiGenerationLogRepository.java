package com.cocky.cockyserver.domain.admin.repository;

import com.cocky.cockyserver.domain.admin.entity.AiGenerationLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiGenerationLogRepository extends JpaRepository<AiGenerationLog, Long> {

    /** 최근 성공 로그의 subtype만 뽑아 스케줄러가 pastTypes(중복 방지 힌트)로 쓴다. */
    List<AiGenerationLog> findTop30BySubtypeIsNotNullOrderByCreatedAtDesc();
}
