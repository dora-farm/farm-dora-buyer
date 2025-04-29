package com.farmdora.farmdorabuyer.orders.dto;

import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.service.NCPObjectStorageService;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        private List<OrderOptionInfo> orderOptions;

        // Review 엔티티를 ReviewResponse DTO로 변환하는 정적 메서드
        public static ReviewResponse fromEntity(Review review, List<ReviewFile> reviewFiles,
                                                List<OrderOptionInfo> orderOptions, NCPObjectStorageService ncpImageService,
                                                List<SaleFile> saleFiles) {
            Sale sale = review.getSale();

            String productImageUrl = "";

            // 해당 sale_id에 맞는 SaleFile 중 isMain=true인 파일 찾기
            Optional<SaleFile> mainSaleFile = saleFiles.stream()
                    .filter(file -> file.isMain())
                    .findFirst();

            if (mainSaleFile.isPresent()) {
                productImageUrl = ncpImageService.getReviewImageUrl(mainSaleFile.get().getSaveFile());
            }

            // 리뷰 이미지 URL 변환
            List<String> imageUrls = reviewFiles.stream()
                    .map(file -> ncpImageService.getReviewImageUrl(file.getSaveFile()))
                    .collect(Collectors.toList());

            return ReviewResponse.builder()
                    .reviewId(review.getId())
                    .saleId(sale.getId())
                    .productName(sale.getTitle())
                    .productImage(productImageUrl)
                    .content(review.getContent())
                    .score(review.getScore())
                    .createdDate(review.getCreatedDate())
                    .imageUrls(imageUrls)
                    .orderOptions(orderOptions)
                    .build();
        }
    }
}