package com.moodmemo.office.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.moodmemo.office.domain.Stamps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class StampDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Dummy {
        private String kakaoId;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime dateTime;
        private String stamp;
        private String memoLet;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String kakaoId;
        private LocalDate localDate;
        private LocalTime localTime;
        private LocalDateTime dateTime;
        private String stamp;
        private String memoLet;

        public static Response fromDocument(Stamps stamps) {
            return Response.builder()
                    .kakaoId(stamps.getKakaoId())
                    .localDate(stamps.getLocalDate())
                    .localTime(stamps.getLocalTime())
                    .dateTime(stamps.getDateTime())
                    .stamp(stamps.getStamp())
                    .memoLet(stamps.getMemoLet())
                    .build();
        }
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Office {
        private String kakaoId;
        private LocalDateTime dateTime;
        private String stamp;
        private String memoLet;

        public static Office fromDocument(Stamps stamps) {
            return Office.builder()
                    .kakaoId(stamps.getKakaoId())
                    .dateTime(stamps.getDateTime())
                    .stamp(stamps.getStamp())
                    .memoLet(stamps.getMemoLet())
                    .build();
        }
    }
}
