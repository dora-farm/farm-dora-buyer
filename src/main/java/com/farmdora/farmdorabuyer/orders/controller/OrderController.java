package com.farmdora.farmdorabuyer.orders.controller;

import com.farmdora.farmdorabuyer.common.response.PageResponseDTO;
import com.farmdora.farmdorabuyer.common.response.ResponseDTO;
import com.farmdora.farmdorabuyer.orders.dto.OrderResponseDTO;
import com.farmdora.farmdorabuyer.orders.dto.OrderSearchDTO;
import com.farmdora.farmdorabuyer.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my/user")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/order")
    public ResponseEntity<?> getOrderList(
            @ModelAttribute OrderSearchDTO orderSearchDTO,
            @PageableDefault(size = 5)Pageable pageable) {
        //jwt 사용시
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Integer userId = ((UserDetails) authentication.getPrincipal()).getUserId();
        int userId = 1;
        PageResponseDTO<OrderResponseDTO> result = orderService.getOrderList(userId, orderSearchDTO, pageable);

        return ResponseEntity.ok(
                ResponseDTO.<PageResponseDTO<OrderResponseDTO>>builder()
                        .status(200)
                        .message("주문 목록 조회 성공")
                        .data(result)
                        .build()
        );
    }
}