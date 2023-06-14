package com.moodmemo.office.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
//@NoArgsConstructor
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "users")
public class Users {

    private String kakaoId;
    private String username;
    private int age;
    private boolean gender;
    private String job;
}
