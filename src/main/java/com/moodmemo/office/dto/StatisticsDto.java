package com.moodmemo.office.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class StatisticsDto {
//    @Getter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Builder
//    public static class Simple {
//        private String kakaoId;
//        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
//        private LocalDateTime dateTime;
//        private String stamp;
//        private String memoLet;
//        private String imageUrl;
//    }
}
