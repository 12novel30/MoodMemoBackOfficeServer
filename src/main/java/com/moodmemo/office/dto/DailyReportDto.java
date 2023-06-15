package com.moodmemo.office.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyReportDto {
    private String kakaoId;
    private String username;
    private LocalDate date;
    private String title, bodyText;
    private List<String> keyword;
}
