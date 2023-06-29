package com.moodmemo.office.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "stamps")
public class Stamps {
    private String id;
    private String kakaoId;
    private LocalDateTime dateTime;
    private String stamp;
    private String memoLet;


}
