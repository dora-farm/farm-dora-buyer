package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.OrderStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Short> {
    Optional<OrderStatus> findByName(String name);
}
