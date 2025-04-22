package com.farmdora.farmdorabuyer.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WishlistDTO {
    private int saleId;
    private String title;
    private String name;
    private Integer price;
    private String saveFile;

    public static WishlistDTO from(Object[] obj) {
        return WishlistDTO.builder()
                .saleId((Integer) obj[0])
                .title((String) obj[1])
                .name((String) obj[2])
                .price((Integer) obj[3])
                .saveFile((String) obj[4])
                .build();
    }
}
