package com.farmdora.farmdorabuyer.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    SEARCH_ORDER_SUCCESS("주문 목록 조회에 성공하였습니다."),
    SEARCH_ORDER_PAY_SUCCESS("주문 결제 정보 조회에 성공하였습니다."),
    REGISTER_REVIEW_SUCCESS("리뷰 등록에 성공하였습니다."),
    CANCEL_ORDER_SUCCESS("주문 취소에 성공하였습니다."),
    ADD_BASKET_SUCCESS("장바구니 추가에 성공하였습니다."),
    GET_BASKETS_SUCCESS("장바구니 목록 조회에 성공하였습니다."),
    REMOVE_BASKET_SUCCESS("장바구니 삭제에 성공하였습니다."),
    UPDATE_BASKET_QUANTITY_SUCCESS("장바구니 수량 수정에 성공하였습니다."),
    CREATE_ORDER_SUCCESS("주문에 성공하였습니다.");

    private final String message;
}