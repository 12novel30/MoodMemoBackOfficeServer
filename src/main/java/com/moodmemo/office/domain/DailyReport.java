package com.moodmemo.office.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "dailyReport")
public class DailyReport {
    private String id; // TODO - userId ? dailyReportId ? -> 구분할 것
    private String kakaoId;
    private String username;
    private LocalDateTime date;
    private String title, bodyText;
    private String keyword1st;
    private String keyword2nd;
    private String keyword3rd;
    private String time;
    private Integer updateByDevCnt;
    private Integer updateByUserCnt;
    private Integer likeCnt;
}
