package com.moodmemo.office.dto;

import com.moodmemo.office.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
