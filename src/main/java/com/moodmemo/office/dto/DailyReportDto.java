package com.moodmemo.office.dto;

import com.moodmemo.office.domain.DailyReport;
import com.moodmemo.office.domain.Stamps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DailyReportDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private UserDto.SendAI userDto;
        private List<Stamps> todayStampList;

    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Simple {
        private String kakaoId;
        private String date; // like 2023-06-30

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String id;
        private String kakaoId;
        private String username;
        private LocalDate date;
        private String title, bodyText;
        private String keyword1st;
        private String keyword2nd;
        private String keyword3rd;
        private String time;
        private Integer likeCnt;

        public static Response fromDocument(DailyReport dailyReport) {
            return Response.builder()
                    .id(dailyReport.getId())
                    .kakaoId(dailyReport.getKakaoId())
                    .username(dailyReport.getUsername())
                    .date(dailyReport.getDate().toLocalDate())
                    .title(dailyReport.getTitle())
                    .bodyText(dailyReport.getBodyText())
                    .keyword1st(dailyReport.getKeyword1st())
                    .keyword2nd(dailyReport.getKeyword2nd())
                    .keyword3rd(dailyReport.getKeyword3rd())
                    .time(dailyReport.getTime())
                    .likeCnt(dailyReport.getLikeCnt())
                    .build();
        }
    }
}
