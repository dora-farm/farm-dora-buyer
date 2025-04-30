package com.farmdora.farmdorabuyer.orders.dto;

import com.farmdora.farmdorabuyer.entity.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPayDetailDTO {

    private Integer orderId;
    private LocalDateTime createDate;
    private String statusName;

    // 판매 상품 목록
    private List<SaleInfoDTO> sales;

    private PayDetailDTO payDetail;
    private SellerDetailDTO sellerDetail;


    public static OrderPayDetailDTO fromEntityWithSales(Order order, Pay pay, List<SaleInfoDTO> sales, Seller seller) {
        return OrderPayDetailDTO.builder()
                .orderId(order.getId())
                .createDate(order.getCreatedDate())
                .statusName(order.getStatus().getName())
                .sales(sales)
                .payDetail(PayDetailDTO.fromEntity(pay))
                .sellerDetail(SellerDetailDTO.fromEntity(seller))
                .build();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PayDetailDTO {
        private Integer payId;
        private String payStatus;
        private String payMethod;
        private Integer amount;
        private String bankName;
        private String card;
        private String accountNum;
        private String payNum;
        private String cardNumber;

        public static PayDetailDTO fromEntity(Pay pay) {
            return PayDetailDTO.builder()
                    .payId(pay.getId())
                    .payStatus(pay.getStatus() != null ? pay.getStatus().getName() : null)
                    .payMethod(pay.getMethod())
                    .amount(pay.getAmount())
                    .bankName(pay.getBankName())
                    .card(pay.getCard())
                    .accountNum(pay.getAccountNum())
                    .payNum(pay.getPayNum())
                    .cardNumber(pay.getCardNumber())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SellerDetailDTO {
        private String companyName;
        private String addr;
        private String detailAddr;
        private String postNum;
        private String saveFile;

        public static SellerDetailDTO fromEntity(Seller seller) {
            return SellerDetailDTO.builder()
                    .companyName(seller.getName())
                    .addr(seller.getAddress() != null ? seller.getAddress().getAddr() : null)
                    .detailAddr(seller.getAddress() != null ? seller.getAddress().getDetailAddr() : null)
                    .postNum(seller.getAddress() != null ? seller.getAddress().getPostNum() : null)
                    .saveFile(seller.getSaveFile())
                    .build();
        }
    }
}