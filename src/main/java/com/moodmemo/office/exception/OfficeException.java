package com.moodmemo.office.exception;

import com.moodmemo.office.code.OfficeErrorCode;
import lombok.Getter;

@Getter
public class OfficeException extends RuntimeException {
    private OfficeErrorCode officeErrorCode;
    private String detailMessage;

    public OfficeException(OfficeErrorCode greenErrorCode) {
        // for 일반적인 에러상황
        super(greenErrorCode.getMessage());
        this.officeErrorCode = greenErrorCode;
        this.detailMessage = greenErrorCode.getMessage();
    }

    public OfficeException(OfficeErrorCode greenErrorCode,
                           String detailMessage) {
        // for 커스텀한 에러메세지를 출력해야할 때 사용
        super(greenErrorCode.getMessage());
        this.officeErrorCode = greenErrorCode;
        this.detailMessage = detailMessage;
    }
}