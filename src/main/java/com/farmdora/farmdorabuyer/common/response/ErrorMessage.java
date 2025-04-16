package com.farmdora.farmdorabuyer.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    ORDER_NOT_FOUND("주문내역을 찾을 수 없습니다."),
    REVIEW_REGISTRATION_FAIL("리뷰 등록에 실패하였습니다.");

    private final String message;
}
