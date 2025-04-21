package com.farmdora.farmdorabuyer.orders.dto;

import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.service.NcpImageService;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewDTO {

    @Getter
    @Setter
    @Builder
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
        private List<OrderOptionInfo> orderOptions; // 주문 옵션 정보 추가

        // Review 엔티티를 ReviewResponse DTO로 변환하는 정적 메서드
        public static ReviewResponse fromEntity(Review review, List<ReviewFile> reviewFiles,
                                                List<OrderOptionInfo> orderOptions, NcpImageService ncpImageService) {
            Sale sale = review.getSale();

            // 파일명을 전체 URL로 변환
            List<String> imageUrls = reviewFiles.stream()
                    .map(file -> ncpImageService.getObjectUrl(file.getSaveFile()))
                    .collect(Collectors.toList());

            // 리뷰의 saleId와 동일한 옵션만 필터링
            List<OrderOptionInfo> filteredOrderOptions = orderOptions.stream()
                    .filter(option -> option.getSaleId().equals(sale.getId()))
                    .collect(Collectors.toList());

            return ReviewResponse.builder()
                    .reviewId(review.getId())
                    .saleId(sale.getId())
                    .productName(sale.getTitle())
                    .productImage(sale.getSeller().getSaveFile())
                    .content(review.getContent())
                    .score(review.getScore())
                    .createdDate(review.getCreatedDate())
                    .imageUrls(imageUrls)
                    .orderOptions(filteredOrderOptions)
                    .build();
        }
    }
}