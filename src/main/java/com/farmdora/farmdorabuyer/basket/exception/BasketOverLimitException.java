package com.farmdora.farmdorabuyer.basket.exception;

import com.farmdora.farmdorabuyer.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BasketOverLimitException extends BaseException {

    public BasketOverLimitException(String message, HttpStatus status) {
        super(message, status);
    }
}
