package com.moodmemo.office.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class OfficeExceptionHandler {
    @ExceptionHandler(OfficeException.class) // DMakerException 관련 에러
    public OfficeErrorResponse handleException(
            OfficeException e,
            HttpServletRequest request) {
        log.error("errorCode: {}, url: {}, message: {}",
                e.getOfficeErrorCode(), request.getRequestURI(), e.getDetailMessage());
        return OfficeErrorResponse.builder()
                .errorCode(e.getOfficeErrorCode())
                .errorMessage(e.getDetailMessage())
                .build();
    }
}