package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ReviewRepositry extends JpaRepository<Review, Integer> {

    // 여러 saleId로 리뷰가 작성된 saleId 목록 조회
    @Query("SELECT r.sale.id FROM Review r WHERE r.sale.id IN (:saleIds)")
    Set<Integer> findSaleIdsWithReviewsBySaleIds(@Param("saleIds") List<Integer> saleIds);
}