package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.entity.Order;
import com.farmdora.farmdorabuyer.entity.Review;
import com.farmdora.farmdorabuyer.entity.ReviewFile;
import com.farmdora.farmdorabuyer.orders.dto.ReviewDTO.*;
import com.farmdora.farmdorabuyer.orders.repository.OrderRepository;
import com.farmdora.farmdorabuyer.orders.repository.ReviewFileRepository;
import com.farmdora.farmdorabuyer.orders.repository.ReviewRepositry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepositry reviewRepositry;
    private final ReviewFileRepository reviewFileRepository;
    private final OrderRepository orderRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/upload/reviews/";

    public ReviewService(ReviewRepositry reviewRepositry, ReviewFileRepository reviewFileRepository, OrderRepository orderRepository) {
        this.reviewRepositry = reviewRepositry;
        this.reviewFileRepository = reviewFileRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, List<MultipartFile> files) throws IOException {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 리뷰 생성 및 저장
        Review review = Review.builder()
                .order(order)
                .score(request.getScore())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepositry.save(review);

        List<String> imageUrls = new ArrayList<>();

        if(files != null && !files.isEmpty()) {
            for(MultipartFile file: files) {
                if(!file.isEmpty()) {
                    try {
                        String originalFilename = file.getOriginalFilename();
                        if (originalFilename == null) continue;

                        String savedFilename = UUID.randomUUID() +
                                originalFilename.substring(originalFilename.lastIndexOf("."));

                        // 저장 경로 설정 및 디렉토리 생성
                        Path uploadPath = Paths.get(uploadDir);
                        File directory = new File(uploadPath.toString());
                        if (!directory.exists()) {
                            boolean created = directory.mkdirs();
                            if (!created) {
                                throw new IOException("디렉토리 생성에 실패했습니다: " + uploadPath);
                            }
                        }

                        // 파일 저장
                        file.transferTo(new File(directory, savedFilename));

                        ReviewFile reviewFile = ReviewFile.builder()
                                .review(savedReview)
                                .originFile(originalFilename)
                                .saveFile(savedFilename)
                                .build();

                        reviewFileRepository.save(reviewFile);

                        imageUrls.add("/images/reviews/" + savedFilename);
                    } catch (Exception e) {
                        // 로깅 추가 권장
                        System.err.println("파일 업로드 중 오류 발생: " + e.getMessage());
                    }
                }
            }
        }

        return ReviewResponse.builder()
                .reviewId(savedReview.getId())  // 오타 수정
                .orderId(savedReview.getOrder().getId())
                .content(savedReview.getContent())
                .score(savedReview.getScore())
                .createdDate(savedReview.getCreatedDate())  // LocalDateTime을 String으로 변환
                .imageUrls(imageUrls)
                .build();
    }
}
