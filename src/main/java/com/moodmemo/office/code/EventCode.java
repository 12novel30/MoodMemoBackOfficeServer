package com.moodmemo.office.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public enum EventCode {
    WEEK1(LocalDate.of(2023, 6, 23),
            LocalDate.of(2023, 6, 29)),
    WEEK2(LocalDate.of(2023, 6, 30),
            LocalDate.of(2023, 7, 6)),
    WEEK3(LocalDate.of(2023, 7, 7),
            LocalDate.of(2023, 7, 13)),
    WEEK4(LocalDate.of(2023, 7, 14),
            LocalDate.of(2023, 7, 20)),
    WEEK5(LocalDate.of(2023, 7, 21),
            LocalDate.of(2023, 7, 27)),
    WEEK_REST(LocalDate.of(2023, 7, 21),
            LocalDate.of(2023, 8, 14)),
    WEEK_FINAL(LocalDate.of(2023, 8, 15),
            LocalDate.of(2023, 8, 21)),
    WEEK_NO_EVENT(LocalDate.of(2023, 8, 22),
            LocalDate.of(2023, 11, 27))

    ;
    private final LocalDate startDate, endDate;
}
