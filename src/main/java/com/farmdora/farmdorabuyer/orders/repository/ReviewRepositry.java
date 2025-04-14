package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ReviewRepositry extends JpaRepository<Review, Integer> {

//    @Query("SELECT r.sale.id FROM Review r WHERE r.user.id IN :userId")
//    Set<Integer> findOrderIdsWithReviews(@Param("userId")List<Integer> userId);

    @Query("SELECT r.sale.id FROM Review r WHERE r.user.userId = :userId")
    Set<Integer> findSaleIdsWithReviewsByUserId(@Param("userId") Integer userId);

//    boolean existsByOrderId(Integer orderId);
}
