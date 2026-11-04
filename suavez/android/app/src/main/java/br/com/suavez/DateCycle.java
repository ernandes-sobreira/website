package br.com.suavez;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Datas civis: o intervalo de 10 dias não depende do horário de verão. */
public final class DateCycle {
    private DateCycle() {}

    public static LocalDate next(LocalDate lastApplication, LocalDate today) {
        LocalDate first = lastApplication.plusDays(10);
        if (!first.isBefore(today)) return first;
        long days = ChronoUnit.DAYS.between(first, today);
        long jumps = (days + 9) / 10;
        return first.plusDays(jumps * 10);
    }
}
