package com.farmdora.farmdorabuyer.orders.dto;

import java.util.List;
import lombok.*;

public class OrderRequestDTO {

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderFromBasketDTO {
        private Integer depotId;
        private List<Integer> basketIds;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderFromOptionDTO {
        private Integer depotId;
        private Integer optionId;
        private Integer quantity;
    }
}
