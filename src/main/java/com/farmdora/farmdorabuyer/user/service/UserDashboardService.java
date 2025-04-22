package com.farmdora.farmdorabuyer.user.service;

import com.farmdora.farmdorabuyer.user.dto.OrderStatusDTO;
import com.farmdora.farmdorabuyer.user.dto.UserDashboardDTO;
import com.farmdora.farmdorabuyer.user.dto.WishlistDTO;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDashboardService {

    UserDashboardDTO getDashboardInfo(@Param("userId") Integer userId);

    List<OrderStatusDTO> getOrderStatusByUserId(@Param("userId") Integer userId);

    List<WishlistDTO> getWishlistByUserId(@Param("userId") Integer userId);
}
