package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.exception.FileException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.common.response.PageResponseDTO;
import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.dto.ReviewDTO.*;
import com.farmdora.farmdorabuyer.orders.dto.SearchDTO;
import com.farmdora.farmdorabuyer.orders.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepositry reviewRepositry;
    private final ReviewFileRepository reviewFileRepository;
    private final SaleRepository saleRepository;
    private final OrderRepository orderRepository;
    private final NcpImageService ncpImageService;
    private final OrderOptionRepository orderOptionRepository;

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

        for(MultipartFile file: files) {
            try {
                String originalFilename = file.getOriginalFilename();
                String savedFilename  = ncpImageService.uploadImage(file);

                // 리뷰 파일 정보 저장
                ReviewFile reviewFile = ReviewFile.builder()
                        .review(savedReview)
                        .originFile(originalFilename)
                        .saveFile(savedFilename)
                        .build();

                reviewFileRepository.save(reviewFile);
            } catch (Exception e) {
                throw new FileException("파일을 저장할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ReviewResponse> getMyReviews(Integer userId, SearchDTO searchDTO, Pageable pageable) {
        Page<Review> reviewPage = reviewRepositry.findAllByOrderUserUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(
                userId,
                searchDTO.getStartDate(),
                searchDTO.getEndDate(),
                pageable);

        // 같은 orderId를 가진 리뷰들을 sale_id 기준으로 그룹화
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .collect(Collectors.groupingBy(
                        review -> review.getOrder().getId(),
                        Collectors.groupingBy(review -> review.getSale().getId())
                ))
                .values().stream()
                .flatMap(orderGroup -> orderGroup.values().stream())
                .map(saleReviews -> {
                    // 같은 주문의 같은 판매 상품에 대한 리뷰 중 첫 번째 리뷰 선택
                    Review review = saleReviews.get(0);
                    List<ReviewFile> reviewFiles = reviewFileRepository.findByReviewId(review.getId());
                    List<OrderOption> orderOptions = orderOptionRepository.findByOrderId(review.getOrder().getId());

                    // 리뷰의 saleId와 동일한 옵션만 필터링
                    List<OrderOptionInfo> filteredOrderOptions = orderOptions.stream()
                            .filter(option -> option.getOption().getSale().getId().equals(review.getSale().getId()))
                            .map(OrderOptionInfo::fromEntity)
                            .toList();
                    return ReviewResponse.fromEntity(review, reviewFiles, filteredOrderOptions, ncpImageService);
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(reviewPage, reviewResponses);
    }

    @Transactional
    public ReviewResponse updateReview(
            Integer reviewId,
            byte score,
            String content,
            List<String> removedImageUrls,
            MultipartFile[] newImages) throws IOException {

        // todo
        // jwt로 userId가져와야함
        Integer userId = 1;

        Review review = reviewRepositry.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("review", reviewId));

        review.setScore(score);
        review.setContent(content);
        reviewRepositry.save(review);

        if(removedImageUrls != null && !removedImageUrls.isEmpty()) {
            for(String imageUrl : removedImageUrls) {
                String reviewFileName = extractFilenameDeleteUrl(imageUrl);
                ReviewFile reviewFile = reviewFileRepository.findBySaveFileAndReviewId(reviewFileName, reviewId);

                ncpImageService.deleteObjectToNCP(reviewFileName);
                reviewFileRepository.delete(reviewFile);
            }
        }

        List<ReviewFile> NewUpdatedReviewFiles = new ArrayList<>(reviewFileRepository.findAllByReviewId(reviewId));
        if (newImages != null) {
            for (MultipartFile image : newImages) {
                String UpdateFileName = ncpImageService.uploadImage(image);

                ReviewFile reviewFile = ReviewFile.builder()
                        .review(review)
                        .originFile(image.getOriginalFilename())
                        .saveFile(UpdateFileName)
                        .build();

                reviewFileRepository.save(reviewFile);
                NewUpdatedReviewFiles.add(reviewFile);
            }
        }

        List<OrderOption> orderOptions = orderOptionRepository.findByOrderId(review.getOrder().getId());

        // 리뷰의 saleId와 동일한 옵션만 필터링
        List<OrderOptionInfo> filteredOrderOptions = orderOptions.stream()
                .filter(option -> option.getOption().getSale().getId().equals(review.getSale().getId()))
                .map(OrderOptionInfo::fromEntity)
                .collect(Collectors.toList());

        // 응답 DTO 생성 및 반환
        return ReviewResponse.fromEntity(review, NewUpdatedReviewFiles, filteredOrderOptions, ncpImageService);
    }

    private String extractFilenameDeleteUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

}