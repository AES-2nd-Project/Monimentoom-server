package com.example.monimentoom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_LOGIN_INPUT_VALUE(HttpStatus.BAD_REQUEST, "AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다."),


    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U002", "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 존재하는 이메일입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U004", "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "U005", "접근 권한이 없습니다."),

    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "방을 찾을 수 없습니다."),
    ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "R002", "권한이 없습니다."),
    CANNOT_DELETE_LAST_ROOM(HttpStatus.BAD_REQUEST, "R003", "마지막 방은 삭제할 수 없습니다."),

    GOODS_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "굿즈를 찾을 수 없습니다."),

    POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "위치정보를 찾을 수 없습니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;


}
