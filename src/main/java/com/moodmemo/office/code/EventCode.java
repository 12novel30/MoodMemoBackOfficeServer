package com.moodmemo.office.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public enum EventCode {
    WEEK1(LocalDate.of(2023, 6, 3),
            LocalDate.of(2023, 6, 30)),
    WEEK2(LocalDate.of(2023, 7, 1),
            LocalDate.of(2023, 7, 2)),
    WEEK3(LocalDate.of(2023, 7, 3),
            LocalDate.of(2023, 7, 4)),
    WEEK4(LocalDate.of(2023, 7, 5),
            LocalDate.of(2023, 7, 6)),

    ;
    private final LocalDate startDate, endDate;
}
