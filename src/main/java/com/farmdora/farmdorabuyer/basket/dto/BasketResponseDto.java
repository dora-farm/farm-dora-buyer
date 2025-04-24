package com.farmdora.farmdorabuyer.basket.dto;

import com.farmdora.farmdorabuyer.entity.Basket;
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

    public static BasketResponseDto fromEntity(Basket basket) {
        return BasketResponseDto.builder()
                .basketId(basket.getId())
                .saleId(basket.getOption().getSale().getId())
                .title(basket.getOption().getSale().getTitle())
                .option(basket.getOption().getName())
                .quantity(basket.getQuantity())
                .price(basket.getOption().getPrice())
                .build();
    }
}
