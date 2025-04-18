package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAllByUserUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(
            Integer userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
}