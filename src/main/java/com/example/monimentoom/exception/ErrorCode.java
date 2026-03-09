package com.example.monimentoom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT_VALUE(400, "C001", "입력값이 올바르지 않습니다."),



    USER_NOT_FOUND(404,"U001", "사용자를 찾을 수 없습니다."),
       ;

    private final int status;
    private final String code;
    private final String message;


}
