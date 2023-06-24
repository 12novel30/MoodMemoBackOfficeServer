package com.moodmemo.office.dto;

import com.moodmemo.office.domain.Users;
import lombok.*;

public class UserDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Dummy {
        private String kakaoId;
        private String username;
        private int age;
        private boolean gender;
        private String job;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StampCount {
        private String kakaoId;
        private String username;
        @Setter
        private int stampCount;
        public static StampCount fromDocuments(Users user) {
            return StampCount.builder()
                    .kakaoId(user.getKakaoId())
                    .username(user.getUsername())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String kakaoId;
        private String username;

        public static Response fromDocuments(Users user) {
            return Response.builder()
                    .kakaoId(user.getKakaoId())
                    .username(user.getUsername())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Detail {
        private String kakaoId;
        private String username;
        private int age;
        private boolean gender;
        private String job;

        public static Detail fromDocuments(Users user) {
            return Detail.builder()
                    .kakaoId(user.getKakaoId())
                    .username(user.getUsername())
                    .age(user.getAge())
                    .job(user.getJob())
                    .gender(user.isGender())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SendAI {
        private String kakaoId;
        private String username;
        private int age;
        private String gender; // 여자, 남자
        private String job;

        public static SendAI fromDocuments(Users user) {
            String gender = user.isGender() ? "남자" : "여자";
            return SendAI.builder()
                    .kakaoId(user.getKakaoId())
                    .username(user.getUsername())
                    .age(user.getAge())
                    .job(user.getJob())
                    .gender(gender)
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Rank {
        private String kakaoId;
        private String username;
        private int week1, week2, week3, week4;

        public static Rank fromDocument(Users user) {
            return Rank.builder()
                    .kakaoId(user.getKakaoId())
                    .username(user.getUsername())
                    .week1(user.getWeek1())
                    .week2(user.getWeek2())
                    .week3(user.getWeek3())
                    .week4(user.getWeek4())
                    .build();
        }

    }
}
