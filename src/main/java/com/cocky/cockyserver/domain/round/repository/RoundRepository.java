package com.cocky.cockyserver.domain.round.repository;

import com.cocky.cockyserver.domain.round.entity.Round;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {

    Optional<Round> findByActiveTrueAndOpenAtLessThanEqualAndCloseAtAfter(
            LocalDateTime openAtInclusive, LocalDateTime closeAtExclusive);
}
