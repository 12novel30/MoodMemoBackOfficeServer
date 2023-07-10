package com.moodmemo.office.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OfficeCode {
    STARTDATE_TAIL(" 23:59:59"),
    ENDDATE_TAIL(" 00:00:00"),
    DEV("dev"),
    USER("user"),

    // 둘 다 사용하는 바
//    LOCAL_FOLDER("/app/photos"), // ec2 folder name - mount 해야한다고 한다
    SEASON_3_FOLDER("season3"), // S3 folder name


    // 남자는 true
    // 여자는 false
    ;
    private final String description;
}
