package com.farmdora.farmdorabuyer.basket.controller;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.ADD_BASKET_SUCCESS;
import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.GET_BASKETS_SUCCESS;
import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.REMOVE_BASKET_SUCCESS;
import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.UPDATE_BASKET_QUANTITY_SUCCESS;

import com.farmdora.farmdorabuyer.basket.dto.BasketRequestDto;
import com.farmdora.farmdorabuyer.basket.dto.BasketResponseDto;
import com.farmdora.farmdorabuyer.basket.service.BasketService;
import com.farmdora.farmdorabuyer.common.response.HttpResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @PostMapping
    public ResponseEntity<?> addBasket(@RequestBody BasketRequestDto basketRequest) {
        // TODO Security 구현 완료 후 대체 예정
        Integer userId = 1;
        basketService.addBasket(userId, basketRequest);
        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, ADD_BASKET_SUCCESS.getMessage(), null));
    }

    @GetMapping
    public ResponseEntity<?> getBaskets() {
        // TODO Security 구현 완료 후 대체 예정
        Integer userId = 1;
        List<BasketResponseDto> baskets = basketService.getBaskets(userId);
        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, GET_BASKETS_SUCCESS.getMessage(), baskets));
    }

    @DeleteMapping("/{basketId}")
    public ResponseEntity<?> removeBasket(@PathVariable("basketId") Integer basketId) {
        // TODO Security 구현 완료 후 대체 예정
        Integer userId = 1;
        basketService.removeBasket(userId, basketId);
        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, REMOVE_BASKET_SUCCESS.getMessage(), null));
    }

    @PutMapping("/{basketId}")
    public ResponseEntity<?> updateQuantity(@PathVariable("basketId") Integer basketId,
                                            @RequestParam int quantity) {
        // TODO Security 구현 완료 후 대체 예정
        Integer userId = 1;
        basketService.updateBasketQuantity(userId, basketId, quantity);
        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, UPDATE_BASKET_QUANTITY_SUCCESS.getMessage(), null));
    }
}
