package com.farmdora.farmdorabuyer.orders.dto;

import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.service.NcpImageService;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RefundDTO {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefundRequest {
        private Integer orderId;
        private Short typeId; // 환불사유
        private Short statusId;
        private String content;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefundResponse {
        private Integer refundId;
        private Integer orderId;
        private String productName;
        private String productImage;
        private String refundTypeName;
        private String content;
        private boolean isProcess;
        private LocalDateTime createdDate;
        private List<String> imageUrls;
        private List<OrderOptionInfo> orderOptions;

        // fromEntity 정적 메서드
        public static RefundResponse fromEntity(Refund refund, List<RefundFile> refundFiles,
                                                List<OrderOption> orderOptions,
                                                Sale sale, SaleFile mainImage,
                                                NcpImageService ncpImageService) {
            // 이미지 URL 리스트 생성
            List<String> imageUrls = refundFiles.stream()
                    .map(file -> ncpImageService.getObjectUrl(file.getSaveFile()))
                    .collect(Collectors.toList());

            // 주문 옵션 정보 변환
            List<OrderOptionInfo> optionInfos = orderOptions.stream()
                    .map(OrderOptionInfo::fromEntity)
                    .collect(Collectors.toList());

            return RefundResponse.builder()
                    .refundId(refund.getId())
                    .orderId(refund.getOrder().getId())
                    .productName(sale != null ? sale.getTitle() : "")
                    .productImage(mainImage != null ? mainImage.getSaveFile() : "")
                    .refundTypeName(refund.getType().getName())
                    .content(refund.getContent())
                    .isProcess(refund.isProcess())
                    .createdDate(refund.getCreatedDate())
                    .imageUrls(imageUrls)
                    .orderOptions(optionInfos)
                    .build();
        }
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

        // fromEntity 정적 메서드
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
    public static class RefundListItem {
        private Integer refundId;
        private Integer orderId;
        private String productName;
        private String productImage;
        private String refundTypeName;
        private boolean isProcess;
        private LocalDateTime createdDate;

        // fromEntity 정적 메서드
        public static RefundListItem fromEntity(Refund refund, Sale sale, SaleFile mainImage) {
            return RefundListItem.builder()
                    .refundId(refund.getId())
                    .orderId(refund.getOrder().getId())
                    .productName(sale != null ? sale.getTitle() : "")
                    .productImage(mainImage != null ? mainImage.getSaveFile() : "")
                    .refundTypeName(refund.getType().getName())
                    .isProcess(refund.isProcess())
                    .createdDate(refund.getCreatedDate())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefundTypeInfo {
        private Short typeId;
        private String name;

        // fromEntity 정적 메서드
        public static RefundTypeInfo fromEntity(RefundType refundType) {
            return RefundTypeInfo.builder()
                    .typeId(refundType.getId())
                    .name(refundType.getName())
                    .build();
        }
    }
}