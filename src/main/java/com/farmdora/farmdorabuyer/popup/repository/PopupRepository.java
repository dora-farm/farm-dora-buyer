package com.farmdora.farmdorabuyer.popup.repository;

import com.farmdora.farmdorabuyer.entity.Popup;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupRepository extends JpaRepository<Popup, Integer> {
    List<Popup> findByEndDateGreaterThanEqual(LocalDateTime now);
}
