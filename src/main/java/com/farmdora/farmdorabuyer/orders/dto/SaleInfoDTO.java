package com.farmdora.farmdorabuyer.orders.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleInfoDTO {
    private Integer saleId;
    private String title;
    private Integer statusId;
    private String saveFile;
    private List<OptionInfoDTO> options;
    private SellerInfoDTO seller;

}
