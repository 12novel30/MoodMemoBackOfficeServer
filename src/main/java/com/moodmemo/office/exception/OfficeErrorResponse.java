package com.moodmemo.office.exception;

import com.moodmemo.office.code.OfficeErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfficeErrorResponse {
    private OfficeErrorCode errorCode;
    private String errorMessage;
}
