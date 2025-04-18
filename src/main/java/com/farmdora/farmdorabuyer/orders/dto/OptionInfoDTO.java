package com.farmdora.farmdorabuyer.orders.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionInfoDTO {
    private String name;
    private Integer quantity;
    private Integer price;
}
