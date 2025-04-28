package com.farmdora.farmdorabuyer.orders.exception;

import com.farmdora.farmdorabuyer.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OutOfStockException extends BaseException {
    public OutOfStockException() {
        super("재고가 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
