package com.example.monimentoom.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }


}
