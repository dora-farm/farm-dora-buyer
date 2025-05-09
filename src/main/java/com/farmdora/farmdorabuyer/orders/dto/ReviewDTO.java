package com.farmdora.farmdorabuyer.orders.dto;

import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.OrderOption;
import com.farmdora.farmdorabuyer.entity.Review;
import com.farmdora.farmdorabuyer.entity.Sale;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class ReviewDTO {

    @Getter
    @Setter
    @Builder
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewRequest {
        private Integer orderId;
        private Integer saleId;
        private byte score;
        private String content;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderOptionInfo {
        private Integer optionId;
        private Integer saleId;
        private String optionName;
        private Integer quantity;
        private Integer price;

        // OrderOption 엔티티를 OrderOptionInfo DTO로 변환하는 정적 메서드
        public static OrderOptionInfo fromEntity(OrderOption orderOption) {
            Option option = orderOption.getOption();

            return OrderOptionInfo.builder()
                    .optionId(option.getId())
                    .saleId(option.getSale().getId())
                    .optionName(option.getName())
                    .quantity(orderOption.getQuantity())
                    .price(orderOption.getPrice())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewResponse {
        private Integer reviewId;
        private Integer saleId;
        private String productName;
        private String productImage;
        private String content;
        private byte score;
        private LocalDateTime createdDate;
        private List<String> imageUrls;
        private List<String> removedImageUrls;
        private List<OrderOptionInfo> orderOptions;

        // Review 엔티티를 ReviewResponse DTO로 변환하는 정적 메서드
        public static ReviewResponse fromEntity(Review review,
                                                List<String> reviewImageUrls,
                                                List<OrderOptionInfo> orderOptions,
                                                String productImageUrl) {
            // 리뷰 이미지 URL 변환
            Sale sale = review.getSale();

            return ReviewResponse.builder()
                    .reviewId(review.getId())
                    .saleId(sale.getId())
                    .productName(sale.getTitle())
                    .productImage(productImageUrl)
                    .content(review.getContent())
                    .score(review.getScore())
                    .createdDate(review.getCreatedDate())
                    .imageUrls(reviewImageUrls)
                    .orderOptions(orderOptions)
                    .build();
        }
    }
}