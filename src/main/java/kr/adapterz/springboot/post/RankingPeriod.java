package kr.adapterz.springboot.post;

import kr.adapterz.springboot.global.exception.BadRequestException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public enum RankingPeriod {
    DAILY,
    WEEKLY;

    public LocalDateTime getStartDateTime() {
        LocalDateTime now = LocalDateTime.now();

        if (this == DAILY) {
            return now.toLocalDate().atStartOfDay();
        }

        return now
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate()
                .atTime(LocalTime.MIN);
    }
}