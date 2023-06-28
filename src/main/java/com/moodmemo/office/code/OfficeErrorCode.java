package com.moodmemo.office.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OfficeErrorCode {
    INTER_SERVER_ERROR(500, "COMMON-ERR-500", "INTER SERVER ERROR"),


    ;
    private int status;
    private String errorCode;
    private String message;
}