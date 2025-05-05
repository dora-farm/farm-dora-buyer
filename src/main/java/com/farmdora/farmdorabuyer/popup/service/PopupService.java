package com.farmdora.farmdorabuyer.popup.service;

import com.farmdora.farmdorabuyer.entity.Popup;
import com.farmdora.farmdorabuyer.popup.dto.PopupDTO;
import com.farmdora.farmdorabuyer.popup.repository.PopupRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PopupService {

    @Value("${ncp.image.path}")
    private String imagePath;

    @Value("${ncp.image.type}")
    private String imageType;

    private final PopupRepository popupRepository;

    @Transactional(readOnly = true)
    public List<PopupDTO> getPopups() {
        List<Popup> popups = popupRepository.findByEndDateGreaterThanEqual(LocalDateTime.now());
        return popups.stream()
                .map(p -> PopupDTO.fromEntity(p, imagePath, imageType))
                .toList();
    }
}
