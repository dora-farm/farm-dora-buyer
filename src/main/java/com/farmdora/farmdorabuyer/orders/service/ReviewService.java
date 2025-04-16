package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.dto.ReviewDTO.*;
import com.farmdora.farmdorabuyer.orders.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepositry reviewRepositry;
    private final ReviewFileRepository reviewFileRepository;
    private final SaleRepository saleRepository;
    private final OrderRepository orderRepository;
    private final NcpImageService ncpImageService;

    @Transactional
    public void createReview(Integer userId, ReviewRequest request, List<MultipartFile> files) throws IOException {

        Sale sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> new ResourceNotFoundException("sale", request.getSaleId()));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("order", request.getOrderId()));

        // 리뷰 생성 및 저장
        Review review = Review.builder()
                .order(order)
                .sale(sale)
                .score(request.getScore())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepositry.save(review);

        if(files != null && !files.isEmpty()) {
            for(MultipartFile file: files) {
                if(!file.isEmpty()) {
                    try {
                        String originalFilename = file.getOriginalFilename();
                        if (originalFilename == null) continue;

                        String savedFilename  = ncpImageService.uploadImage(file);

                        // 리뷰 파일 정보 저장
                        ReviewFile reviewFile = ReviewFile.builder()
                                .review(savedReview)
                                .originFile(originalFilename)
                                .saveFile(savedFilename) // Image Optimizer URL 저장
                                .build();

                        reviewFileRepository.save(reviewFile);

                    } catch (Exception e) {
                        throw new IOException("리뷰 이미지 업로드 실패: " + e.getMessage(), e);
                    }
                }
            }
        }
    }
}