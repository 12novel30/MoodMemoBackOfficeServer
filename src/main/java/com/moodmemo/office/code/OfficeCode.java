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

    LOCAL_FOLDER("tmpImage"),
    // S3 folder name,
    SEASON_3_FOLDER("season3")


    // 남자는 true
    // 여자는 false
    ;
    private final String description;
}
