package com.farmdora.farmdorabuyer.orders.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewDTO {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewRequest {
        private Integer saleId;
        private byte score;
        private String content;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewResponse {
        private Integer reviewId;
        private Integer saleId;
        private String content;
        private byte score;
        private LocalDateTime createdDate;
        private List<String> imageUrls;
    }
}
