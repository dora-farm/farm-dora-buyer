package com.farmdora.farmdorabuyer.orders.controller;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.CREATE_ORDER_SUCCESS;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.farmdora.farmdorabuyer.ControllerTest;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromBasketDTO;
import com.farmdora.farmdorabuyer.orders.exception.NotUserOfDepotException;
import com.farmdora.farmdorabuyer.orders.exception.OutOfStockException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PurchaseControllerTest extends ControllerTest {

    private static final String BASE_URL = "/api/buyer";

    @Nested
    @DisplayName("장바구니 목록으로 주문 API 테스트")
    class OrderFromBasketsTests {

        private static final String ORDER_BASKET_URL = BASE_URL + "/order/basket";

        private static final OrderFromBasketDTO orderRequest = OrderFromBasketDTO.builder()
                .basketIds(List.of(1, 2))
                .depotId(1)
                .build();

        @Test
        @DisplayName("주문 성공")
        void testOrder() throws Exception {
            // given
            doNothing().when(purchaseService).orderFromBaskets(anyInt(), any(OrderFromBasketDTO.class));

            // when
            // then
            mvc.perform(post(ORDER_BASKET_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", equalTo(200)))
                    .andExpect(jsonPath("$.message", equalTo(CREATE_ORDER_SUCCESS.getMessage())));
        }

        @Test
        @DisplayName("주문시 사용자가 존재하지 않을 경우 예외 처리 테스트")
        void testOrder_UserNotFound() throws Exception {
            // given
            doThrow(new ResourceNotFoundException("User", 1)).when(purchaseService).orderFromBaskets(anyInt(), any(OrderFromBasketDTO.class));

            // when
            // then
            mvc.perform(post(ORDER_BASKET_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo("User 데이터가 존재하지 않습니다 : '1'")));
        }

        @Test
        @DisplayName("주문시 배송지가 존재하지 않을 경우 예외 처리 테스트")
        void testOrder_DepotNotFound() throws Exception {
            // given
            doThrow(new ResourceNotFoundException("Depot", 1)).when(purchaseService).orderFromBaskets(anyInt(), any(OrderFromBasketDTO.class));

            // when
            // then
            mvc.perform(post(ORDER_BASKET_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo("Depot 데이터가 존재하지 않습니다 : '1'")));
        }

        @Test
        @DisplayName("주문시 사용자의 배송지가 아닐 경우 예외 처리 테스트")
        void testOrder_NotDepotOfUser() throws Exception {
            // given
            doThrow(new NotUserOfDepotException()).when(purchaseService).orderFromBaskets(anyInt(), any(OrderFromBasketDTO.class));

            // when
            // then
            mvc.perform(post(ORDER_BASKET_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo("사용자의 배송지가 아닙니다.")));
        }

        @Test
        @DisplayName("주문시 설정할 배송상태가 존재하지 않을 경우 예외 처리 테스트")
        void testOrder_OrderStatusNotFound() throws Exception {
            // given
            doThrow(new ResourceNotFoundException("OrderStatus", 1)).when(purchaseService).orderFromBaskets(anyInt(), any(OrderFromBasketDTO.class));

            // when
            // then
            mvc.perform(post(ORDER_BASKET_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo("OrderStatus 데이터가 존재하지 않습니다 : '1'")));
        }

        @Test
        @DisplayName("주문시 주문 수량이 재고를 초과한 경우 예외 처리 테스트")
        void testOrder_OutOfStock() throws Exception {
            // given
            doThrow(new OutOfStockException()).when(purchaseService).orderFromBaskets(anyInt(), any(OrderFromBasketDTO.class));

            // when
            // then
            mvc.perform(post(ORDER_BASKET_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)))
                    .andExpect(jsonPath("$.message", equalTo("재고가 없습니다.")));
        }
    }
}