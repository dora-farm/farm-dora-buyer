package com.farmdora.farmdorabuyer.orders.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerInfoDTO {
    private Integer sellerId;
    private String companyName;
    private String addr;
    private String detailAddr;
    private String companyNum;
    private String phoneNum;
    private String postNum;
}