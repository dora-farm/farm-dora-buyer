package com.farmdora.farmdorabuyer.user.dto;

import com.farmdora.farmdorabuyer.entity.User;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardDTO {
    private UserInfoDTO userInfoDTO;
    private ActivityInfoDTO activityInfoDTO;

    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDTO {
        private String name;
        private String phone;
        private String email;

        public static UserInfoDTO from(User user) {
            return UserInfoDTO.builder()
                    .name(user.getName())
                    .phone(user.getPhoneNum())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Setter
    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityInfoDTO {
        private Long totalAmount;
        private Long reviewCount;
        private Long inquiryCount;
    }
}
