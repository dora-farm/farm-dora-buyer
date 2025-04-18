package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.ReviewFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewFileRepository extends JpaRepository<ReviewFile, Integer> {

    List<ReviewFile> findByReviewId(Integer id);

    ReviewFile findBySaveFileAndReviewId(String reviewFileName, Integer reviewId);

    List<ReviewFile> findAllByReviewId(Integer reviewId);
}