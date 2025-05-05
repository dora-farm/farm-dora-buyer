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

    public static PopupDTO fromEntity(Popup popup, String imagePath, String imageType) {
        return PopupDTO.builder()
                .id(popup.getId())
                .imageUrl(String.format("%s%s%s", imagePath, popup.getSaveFile(), imageType))
                .build();
    }
}
