package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ReviewRepositry extends JpaRepository<Review, Integer> {

    @Query("SELECT r.order.id FROM Review r WHERE r.order.id IN :orderIds")
    Set<Integer> findOrderIdsWithReviews(@Param("orderIds")List<Integer> orderIds);

    boolean existsByOrderId(Integer orderId);
}
