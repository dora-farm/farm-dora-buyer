package com.farmdora.farmdorabuyer.popup.dto;

import com.farmdora.farmdorabuyer.entity.Popup;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PopupDTO {
    private Integer id;
    private String imageUrl;

    public static PopupDTO fromEntity(Popup popup, String imageUrl) {
        return PopupDTO.builder()
                .id(popup.getId())
                .imageUrl(imageUrl)
                .build();
    }
}
