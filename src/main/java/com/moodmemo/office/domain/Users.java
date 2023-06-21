package com.moodmemo.office.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
//@NoArgsConstructor
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "users")
public class Users {

    private String id;
    private String kakaoId;
    private String username;
    private int age;
    private boolean gender;
    private String job;
    private int week1, week2, week3, week4;
}
