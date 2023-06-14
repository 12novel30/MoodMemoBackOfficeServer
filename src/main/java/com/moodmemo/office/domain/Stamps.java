package com.moodmemo.office.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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
