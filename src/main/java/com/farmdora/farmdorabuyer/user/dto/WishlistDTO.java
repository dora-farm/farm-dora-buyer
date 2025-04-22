package com.farmdora.farmdorabuyer.user.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WishlistDTO {
    private int saleId;
    private String title;
    private String option;
    private Integer price;
    private String saveFile;
    private Double score;
    private Long reviewCount;

    public static WishlistDTO from(Object[] obj) {
        return WishlistDTO.builder()
                .saleId((Integer) obj[0])
                .title((String) obj[1])
                .option((String) obj[2])
                .price((Integer) obj[3])
                .saveFile((String) obj[4])
                .score(obj[5] != null ? ((BigDecimal) obj[5]).doubleValue() : null)
                .reviewCount((Long) obj[6])
                .build();
    }
}