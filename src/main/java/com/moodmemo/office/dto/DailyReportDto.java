package com.moodmemo.office.dto;

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
        private UserDto.Detail userDto;
        private List<Stamps> todayStampList;

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String kakaoId;
        private String username;
        private LocalDate date;
        private String title, bodyText;
        private List<String> keyword;
    }
}
