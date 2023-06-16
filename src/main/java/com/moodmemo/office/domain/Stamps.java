package com.moodmemo.office.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@Builder
//@NoArgsConstructor
//@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "stamps")
public class Stamps {

    private String kakaoId;
    private LocalDate localDate;
    private LocalTime localTime;
    private LocalDateTime dateTime;
    private String stamp;
    private String memoLet;


}
