package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.Order;
import com.farmdora.farmdorabuyer.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Integer> {
    // 여러 주문에 대한 결제 정보 한 번에 조회
    List<Pay> findByOrderIn(List<Order> orders);
}
