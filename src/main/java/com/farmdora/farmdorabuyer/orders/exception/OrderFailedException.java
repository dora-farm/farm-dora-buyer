package com.farmdora.farmdorabuyer.orders.exception;

import com.farmdora.farmdorabuyer.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderFailedException extends BaseException {
    
    public OrderFailedException() {
        super("현재 해당 상품의 재고를 처리 중입니다. 잠시 후 다시 시도해주세요.", HttpStatus.CONFLICT);
    }
}
