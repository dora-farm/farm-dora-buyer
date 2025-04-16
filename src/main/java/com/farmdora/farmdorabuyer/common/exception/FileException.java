package com.farmdora.farmdorabuyer.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileException extends BaseException {

    public FileException(String message, HttpStatus status) {
        super(message, status);
    }
}
