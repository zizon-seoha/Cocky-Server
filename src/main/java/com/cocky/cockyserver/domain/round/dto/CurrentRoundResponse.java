package com.cocky.cockyserver.domain.round.dto;

import com.cocky.cockyserver.domain.round.entity.Round;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

public record CurrentRoundResponse(Long roundId, LocalDateTime openAt, LocalDateTime closeAt, boolean isSunday) {

    public static CurrentRoundResponse from(Round round) {
        boolean isSunday = round.getRoundDate().getDayOfWeek() == DayOfWeek.SUNDAY;
        return new CurrentRoundResponse(round.getId(), round.getOpenAt(), round.getCloseAt(), isSunday);
    }
}
