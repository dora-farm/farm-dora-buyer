package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.dto.ReviewDTO.*;
import com.farmdora.farmdorabuyer.orders.repository.*;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepositry reviewRepositry;
    private final ReviewFileRepository reviewFileRepository;
    private final OrderRepository orderRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/upload/reviews/";

    @Transactional
    public void createReview(Integer userId, ReviewRequest request, List<MultipartFile> files) throws IOException {

        Sale sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // User 조회 추가
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리뷰 생성 및 저장 - user 필드 추가
        Review review = Review.builder()
                .user(user)    // User 객체 설정
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
                    } catch (Exception e) {
                        // 로깅 추가 권장
                        System.err.println("파일 업로드 중 오류 발생: " + e.getMessage());
                    }
                }
            }
        }
    }
}
