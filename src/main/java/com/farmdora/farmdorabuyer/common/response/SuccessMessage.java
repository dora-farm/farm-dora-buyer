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
    SEARCH_SALESTATUS_SUCCESS("매출 현황 조회에 성공하였습니다."),
    SEARCH_STATUSRATIO_SUCCESS("반품 및 교환율 조회에 성공하였습니다."),
    SEARCH_PRODUCTRATIO_SUCCESS("제품별 판매 비율 조회에 성공하였습니다."),
    SEARCH_TOTALSALES_SUCCESS("사이트 매출 조회에 성공하였습니다."),
    SEARCH_TOTALUSERS_SUCCESS("사이트 가입자 수 조회에 성공하였습니다."),
    SEARCH_USERDASHBOARDINFO_SUCCESS("내 정보 조회에 성공하였습니다."),
    SEARCH_ORDERSTATUSINFO_SUCCESS("주문 현황 조회에 성공하였습니다."),
    SEARCH_WISHLIST_SUCCESS("찜 리스트 조회에 성공하였습니다.");


    private final String message;
}