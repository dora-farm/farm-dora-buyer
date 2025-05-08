package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.PayStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayStatusRepository extends JpaRepository<PayStatus, Short> {
    Optional<PayStatus> findByName(String name);
}
