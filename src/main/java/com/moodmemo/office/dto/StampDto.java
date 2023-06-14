package com.moodmemo.office.dto;

import com.moodmemo.office.domain.Stamps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class StampDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Dummy {
        private String kakaoId;
        private Date date;
        private String stamp;
        private String memoLet;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String kakaoId;
        private Date date;
        private String stamp;
        private String memoLet;

        public static Response fromDocument(Stamps stamps) {
            return StampDto.Response.builder()
                    .kakaoId(stamps.getKakaoId())
                    .date(stamps.getDate())
                    .stamp(stamps.getStamp())
                    .memoLet(stamps.getMemoLet())
                    .build();
        }
    }
}
