package com.farmdora.farmdorabuyer.orders.controller;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.CREATE_ORDER_SUCCESS;

import com.farmdora.farmdorabuyer.common.response.HttpResponse;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromBasketDTO;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromOptionDTO;
import com.farmdora.farmdorabuyer.orders.service.PurchaseService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping("/basket")
    public ResponseEntity<?> order(Principal principal, @RequestBody OrderFromBasketDTO orderRequest) {
        Integer userId = Integer.parseInt(principal.getName());
        purchaseService.orderFromBaskets(userId, orderRequest);
        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, CREATE_ORDER_SUCCESS.getMessage(), null));
    }

    @PostMapping("/option")
    public ResponseEntity<?> order(Principal principal, @RequestBody OrderFromOptionDTO orderRequest) {
        Integer userId = Integer.parseInt(principal.getName());
        purchaseService.orderFromOption(userId, orderRequest);
        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, CREATE_ORDER_SUCCESS.getMessage(), null));
    }
}
