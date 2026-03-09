package com.example.monimentoom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
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
                errorCode.getMessage()
        );
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    // 에러 응답 DTO
    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private HttpStatus status;
        private String message;
    }
}
