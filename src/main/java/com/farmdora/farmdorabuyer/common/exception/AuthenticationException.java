package com.farmdora.farmdorabuyer.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationException extends BaseException {
    public AuthenticationException() {
        super("로그인에 실패하였습니다.", HttpStatus.UNAUTHORIZED);
    }
}

