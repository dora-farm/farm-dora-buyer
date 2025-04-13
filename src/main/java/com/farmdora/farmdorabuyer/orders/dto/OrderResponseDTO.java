package com.farmdora.farmdorabuyer.orders.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Integer orderId; // 주문 번호
    private LocalDateTime createdDate; // 주문 시간
    private short statusId; // 주문상태

    private Integer saleId;
    private String title; // 상품명

    // private boolean is_main;
    private String saveFile;
    private Integer amount; // 결제 가격
    private List<OptionInfoDTO> options;
    private boolean reviewCompleted; // 리뷰 작성 확인용

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionInfoDTO {
        private String name; // 상품 옵션명
        private Integer quantity; // 구매 수량
    }
}