package com.farmdora.farmdorabuyer.orders.exception;

import com.farmdora.farmdorabuyer.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotUserOfDepotException extends BaseException {
    public NotUserOfDepotException() {
        super("사용자의 배송지가 아닙니다.", HttpStatus.BAD_REQUEST);
    }
}
