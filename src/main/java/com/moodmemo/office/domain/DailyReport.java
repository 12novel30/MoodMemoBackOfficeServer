package com.moodmemo.office.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "dailyReport")
public class DailyReport {
    private String id;
    private String kakaoId;
    private LocalDate date;
    private String title, bodyText;
    private List<String> keyword;
}
