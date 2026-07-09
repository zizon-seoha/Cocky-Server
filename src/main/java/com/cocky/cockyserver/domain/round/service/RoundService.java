package com.cocky.cockyserver.domain.round.service;

import com.cocky.cockyserver.domain.round.dto.CurrentRoundResponse;
import com.cocky.cockyserver.domain.round.entity.Round;
import com.cocky.cockyserver.domain.round.exception.RoundNotFoundException;
import com.cocky.cockyserver.domain.round.repository.RoundRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public CurrentRoundResponse getCurrentRound() {
        return CurrentRoundResponse.from(getCurrentActiveRound());
    }

    /** 다른 도메인 서비스에서 "현재 열려있는 회차" 엔티티가 필요할 때 재사용한다. */
    @Transactional(readOnly = true)
    public Round getCurrentActiveRound() {
        LocalDateTime now = LocalDateTime.now(clock);
        return roundRepository.findByActiveTrueAndOpenAtLessThanEqualAndCloseAtAfter(now, now)
                .orElseThrow(() -> new RoundNotFoundException("현재 열려있는 회차가 없습니다."));
    }
}
