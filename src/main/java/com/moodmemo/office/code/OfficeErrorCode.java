package com.moodmemo.office.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OfficeErrorCode {
    NO_USER(500, "USER-500", "There is no USER"),
    NO_DR(500, "DR-500", "There is no DailyReport"),
    NO_STAMP(500, "STAMP-500", "There is no Stamp"),
    INTER_SERVER_ERROR(500, "COMMON-ERR-500", "INTER SERVER ERROR"),


    ;
    private int status;
    private String errorCode;
    private String message;
}