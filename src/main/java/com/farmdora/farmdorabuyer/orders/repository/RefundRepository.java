package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RefundRepository extends JpaRepository<Refund, Integer> {

    Page<Refund> findAllByOrderUserUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(
            Integer userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
}
