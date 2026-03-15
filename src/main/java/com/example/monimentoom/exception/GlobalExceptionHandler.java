package com.example.monimentoom.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

        // DB가 던진 진짜 에러 메시지
        String errorMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        // 에러 메세지 내용 보고 닉네임 문제로 세분화
        if (errorMessage.contains("nickname")) {
            ErrorCode errorCode = ErrorCode.DUPLICATE_NICKNAME;
            return ResponseEntity
                    .status(errorCode.getStatus())
                    .body(new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage()));
        }

        // 닉네임 중복이 아닌 다른 알 수 없는 DB 에러인 경우 - 범용 에러 메세지
        ErrorCode defaultError = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(defaultError.getStatus())
                .body(new ErrorResponse(defaultError.getStatus(), defaultError.getCode(), defaultError.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "V001", message));
    }
}
