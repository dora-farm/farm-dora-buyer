package com.farmdora.farmdorabuyer.popup.service;

import com.farmdora.farmdorabuyer.common.util.NcpImageProperties;
import com.farmdora.farmdorabuyer.entity.Popup;
import com.farmdora.farmdorabuyer.popup.dto.PopupDTO;
import com.farmdora.farmdorabuyer.popup.repository.PopupRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository popupRepository;
    private final NcpImageProperties imageProperties;

    @Transactional(readOnly = true)
    public List<PopupDTO> getPopups() {
        List<Popup> popups = popupRepository.findValidPopups(LocalDateTime.now(), "배너");
        return popups.stream()
                .map(p -> PopupDTO.fromEntity(p, imageProperties.getBanner().createImageUrl(p.getSaveFile())))
                .toList();
    }
}
