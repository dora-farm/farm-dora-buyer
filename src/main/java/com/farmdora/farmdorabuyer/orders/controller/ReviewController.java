package com.farmdora.farmdorabuyer.orders.controller;

import com.farmdora.farmdorabuyer.common.response.ResponseDTO;
import com.farmdora.farmdorabuyer.orders.dto.ReviewDTO.*;
import com.farmdora.farmdorabuyer.orders.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/my/user")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/order/review")
    public ResponseEntity<?> createReview(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("content") String content,
            @RequestParam("score") Byte score,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {

        try {
            ReviewRequest request = new ReviewRequest();
            request.setOrderId(orderId);
            request.setContent(content);
            request.setScore(score);

            List<MultipartFile> imagesList = images != null ? Arrays.asList(images) : new ArrayList<>();
            ReviewResponse response = reviewService.createReview(request, imagesList);

            return ResponseEntity.ok(
                    ResponseDTO.<ReviewResponse>builder()
                            .status(200)
                            .message("리뷰가 성공적으로 등록되었습니다.")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDTO.builder()
                            .status(500)
                            .message("리뷰 등록 중 오류가 발생했습니다: " + e.getMessage())
                            .build()
            );
        }
    }
}
