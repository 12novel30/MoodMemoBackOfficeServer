package com.moodmemo.office.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.moodmemo.office.domain.Stamps;
import lombok.*;

import java.time.LocalDateTime;

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
        private String imageUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String id; // TODO - updateStampTime 로직 변경해서 dto 안쓰게 되면 지울 것
        private String kakaoId;
        private LocalDateTime dateTime;
        private String stamp;
        private String memoLet;

        public static Response fromDocument(Stamps stamps) {
            return Response.builder()
                    .id(stamps.getId())
                    .kakaoId(stamps.getKakaoId())
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Image {
        private String imageUrl;

        public static Image fromDocument(Stamps stamps) {
            return Image.builder()
                    .imageUrl(stamps.getImageUrl())
                    .build();
        }
    }
}
