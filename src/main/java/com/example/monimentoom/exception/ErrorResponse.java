package com.example.monimentoom.exception;

import org.springframework.http.HttpStatus;

// 에러 응답 DTO
public record ErrorResponse(
        HttpStatus status,
        String code,
        String message
) {}
