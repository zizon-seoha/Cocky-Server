package com.cocky.cockyserver.domain.round.service;

import com.cocky.cockyserver.domain.round.dto.CurrentRoundResponse;
import com.cocky.cockyserver.domain.round.entity.Round;
import com.cocky.cockyserver.domain.round.exception.RoundNotFoundException;
import com.cocky.cockyserver.domain.round.repository.RoundRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoundService {

    private final RoundRepository roundRepository;

    public RoundService(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    @Transactional(readOnly = true)
    public CurrentRoundResponse getCurrentRound() {
        LocalDateTime now = LocalDateTime.now();
        Round round = roundRepository.findByActiveTrueAndOpenAtLessThanEqualAndCloseAtAfter(now, now)
                .orElseThrow(() -> new RoundNotFoundException("현재 열려있는 회차가 없습니다."));
        return CurrentRoundResponse.from(round);
    }
}
