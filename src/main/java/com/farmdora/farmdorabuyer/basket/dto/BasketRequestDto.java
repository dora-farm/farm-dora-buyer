package com.farmdora.farmdorabuyer.basket.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BasketRequestDto {
    private Integer optionId;
    private Integer quantity;
}
