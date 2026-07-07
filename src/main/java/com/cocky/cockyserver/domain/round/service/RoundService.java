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
        LocalDateTime now = LocalDateTime.now(clock);
        Round round = roundRepository.findByActiveTrueAndOpenAtLessThanEqualAndCloseAtAfter(now, now)
                .orElseThrow(() -> new RoundNotFoundException("현재 열려있는 회차가 없습니다."));
        return CurrentRoundResponse.from(round);
    }
}
