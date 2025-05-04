package com.farmdora.farmdorabuyer.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BasketResponseDto {
    private Integer basketId;
    private Integer saleId;
    private String title;
    private String option;
    private Integer quantity;
    private Integer price;
    private String imageUrl;
}
