package com.farmdora.farmdorabuyer.basket.exception;

import com.farmdora.farmdorabuyer.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class QuantityOverLimitException extends BaseException {
    public QuantityOverLimitException() {
        super("옵션의 수량보다 많이 담을 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
