package com.farmdora.farmdorabuyer.user.service;

import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.user.dto.OrderStatusDTO;
import com.farmdora.farmdorabuyer.user.dto.UserDashboardDTO;
import com.farmdora.farmdorabuyer.user.dto.UserDashboardDTO.*;
import com.farmdora.farmdorabuyer.user.dto.WishlistDTO;
import com.farmdora.farmdorabuyer.user.repository.UserDashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDashboardServiceImpl implements UserDashboardService {

    private final UserDashboardRepository userDashboardRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDashboardDTO getDashboardInfo(Integer userId) {

        User userInfo = userDashboardRepository.findById(userId).orElse(null);
        UserInfoDTO userInfoDTO = UserInfoDTO.from(userInfo);

        Long totalAmount = userDashboardRepository.sumTotalAmount(userId);
        Long reviewCount = userDashboardRepository.countReviewsByUserId(userId);
        Long inquiryCount = userDashboardRepository.countInquiriesByUserId(userId);
        ActivityInfoDTO activityInfoDTO = ActivityInfoDTO.builder()
                .totalAmount(totalAmount != null ? totalAmount : 0L)
                .reviewCount(reviewCount != null ? reviewCount : 0L)
                .inquiryCount(inquiryCount != null ? inquiryCount : 0L)
                .build();

        return UserDashboardDTO.builder()
                .userInfoDTO(userInfoDTO)
                .activityInfoDTO(activityInfoDTO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusDTO> getOrderStatusByUserId(Integer userId) {

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return userDashboardRepository.findOrderStatusByUserId(userId, startDateTime, endDateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistDTO> getWishlistByUserId(Integer userId) {

        List<Object[]> results = userDashboardRepository.findWishlistByUserId(userId);
        log.info("results: {}", results);

        return results.stream()
                .map(WishlistDTO::from)
                .collect(Collectors.toList());
    }
}