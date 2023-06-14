package com.moodmemo.office.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
//@NoArgsConstructor
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "stamps")
public class Stamps {

    @Id
    private String kakaoId;
    private Date date;
    private String stamp;
    private String memoLet;


}
