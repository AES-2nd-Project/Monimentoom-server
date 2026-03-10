package com.example.monimentoom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 에러 응답 DTO
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String code;
    private String message;
}