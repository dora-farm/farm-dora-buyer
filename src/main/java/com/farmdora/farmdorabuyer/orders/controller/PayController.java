package com.farmdora.farmdorabuyer.orders.controller;

import com.farmdora.farmdorabuyer.common.response.HttpResponse;
import com.farmdora.farmdorabuyer.orders.dto.OrderPayDetailDTO;
import com.farmdora.farmdorabuyer.orders.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.SEARCH_ORDER_PAY_SUCCESS;

@RestController
@RequestMapping("/api/my/user")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @GetMapping("/order/pay")
    public ResponseEntity<?> getOrderPayDetailInfo(@RequestParam Integer orderId) {

        OrderPayDetailDTO result = payService.getOrderPayDetail(orderId);

        return ResponseEntity
                .ok()
                .body(new HttpResponse(HttpStatus.OK, SEARCH_ORDER_PAY_SUCCESS.getMessage(), result));
    }
}
