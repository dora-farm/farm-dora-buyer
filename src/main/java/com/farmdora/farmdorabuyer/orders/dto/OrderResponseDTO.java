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
    private Integer orderId;
    private LocalDateTime createdDate;
    private Integer amount;
    private List<SaleInfoDTO> sales;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleInfoDTO {
        private Integer saleId;
        private String title;
        private short statusId;
        private boolean reviewCompleted;
        private String saveFile;
        private List<OptionInfoDTO> options;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionInfoDTO {
        private String name;
        private Integer quantity;
        private Integer price;
    }
}