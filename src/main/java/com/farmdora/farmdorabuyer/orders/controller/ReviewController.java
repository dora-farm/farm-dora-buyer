package com.farmdora.farmdorabuyer.orders.controller;

import com.farmdora.farmdorabuyer.common.response.HttpResponse;
import com.farmdora.farmdorabuyer.orders.dto.ReviewDTO.*;
import com.farmdora.farmdorabuyer.orders.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.REGISTER_REVIEW_SUCCESS;

@RestController
@RequestMapping("/api/my/user")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/order/review")
    public ResponseEntity<?> createReview(
            ReviewRequest request,
            @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {

            List<MultipartFile> imagesList = images != null ? Arrays.asList(images) : new ArrayList<>();
            reviewService.createReview(1, request, imagesList);

            return ResponseEntity
                    .ok()
                    .body(new HttpResponse(HttpStatus.OK, REGISTER_REVIEW_SUCCESS.getMessage(), null));

    }
}
