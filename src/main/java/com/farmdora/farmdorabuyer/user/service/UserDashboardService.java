package com.farmdora.farmdorabuyer.user.service;

import com.farmdora.farmdorabuyer.user.dto.OrderStatusDTO;
import com.farmdora.farmdorabuyer.user.dto.UserDashboardDTO;
import com.farmdora.farmdorabuyer.user.dto.WishlistDTO;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDashboardService {

    UserDashboardDTO getDashboardInfo(Integer userId);

    List<OrderStatusDTO> getOrderStatusByUserId(Integer userId);

    List<WishlistDTO> getWishlistByUserId(Integer userId);
}
