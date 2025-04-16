package com.farmdora.farmdorabuyer.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceAlreadyExistsException extends BaseException {
    private final Object data;

    public ResourceAlreadyExistsException(String resourceName, Object data) {
        super(String.format("%s 이미 존재하는 데이터입니다. : '%s'", resourceName, data), HttpStatus.CONFLICT);
        this.data = data;
    }
}
