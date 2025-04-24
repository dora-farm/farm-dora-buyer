package com.farmdora.farmdorabuyer.basket.controller;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.ADD_BASKET_SUCCESS;
import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.GET_BASKETS_SUCCESS;
import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.REMOVE_BASKET_SUCCESS;
import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.UPDATE_BASKET_QUANTITY_SUCCESS;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.farmdora.farmdorabuyer.basket.dto.BasketRequestDto;
import com.farmdora.farmdorabuyer.basket.dto.BasketResponseDto;
import com.farmdora.farmdorabuyer.basket.service.BasketService;
import com.farmdora.farmdorabuyer.common.exception.ResourceAlreadyExistsException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BasketController.class)
class BasketControllerTest {

    @MockitoBean
    private BasketService basketService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("장바구니 추가 API 테스트")
    void testAddBasket() throws Exception {
        // given
        doNothing().when(basketService).addBasket(anyInt(), any(BasketRequestDto.class));

        // when
        // then
        BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                .optionId(1)
                .quantity(10)
                .build();
        mvc.perform(post("/api/basket")
                        .content(new ObjectMapper().writeValueAsString(basketAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo(ADD_BASKET_SUCCESS.getMessage())));
    }

    @Test
    @DisplayName("장바구니 추가시 옵션이 존재하지 않을 경우 에러처리 테스트")
    void testAddBasket_OptionNotFoundException() throws Exception {
        // given
        doThrow(new ResourceNotFoundException("Option", 1)).when(basketService).addBasket(anyInt(), any(BasketRequestDto.class));

        // when
        // then
        BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                .optionId(1)
                .quantity(10)
                .build();
        mvc.perform(post("/api/basket")
                .content(new ObjectMapper().writeValueAsString(basketAddRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Option 데이터가 존재하지 않습니다 : '1'")));
    }

    @Test
    @DisplayName("장바구니 추가시 장바구니가 이미 존재할 경우 에러처리 테스트")
    void testAddBasket_ResourceAlreadyExistsException() throws Exception {
        // given
        doThrow(new ResourceAlreadyExistsException("Basket", 1)).when(basketService).addBasket(anyInt(), any(BasketRequestDto.class));

        // when
        // then
        BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                .optionId(1)
                .quantity(10)
                .build();
        mvc.perform(post("/api/basket")
                        .content(new ObjectMapper().writeValueAsString(basketAddRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", equalTo(409)))
                .andExpect(jsonPath("$.message", equalTo("Basket 이미 존재하는 데이터입니다. : '1'")));
    }

    @Test
    @DisplayName("사용자의 장바구니 목록 조회 API 테스트")
    void testGetBaskets() throws Exception {
        // given
        List<BasketResponseDto> baskets = List.of(
                BasketResponseDto.builder()
                        .basketId(1)
                        .title("상품1")
                        .option("옵션1")
                        .quantity(1)
                        .price(1000)
                        .build(),
                BasketResponseDto.builder()
                        .basketId(2)
                        .title("상품2")
                        .option("옵션2")
                        .quantity(2)
                        .price(2000)
                        .build()
        );
        when(basketService.getBaskets(anyInt())).thenReturn(baskets);

        // when
        // then
        mvc.perform(get("/api/basket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo(GET_BASKETS_SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.size()", equalTo(2)));
    }

    @Test
    @DisplayName("사용자의 장바구니 삭제 API 테스트")
    void testRemoveBasket() throws Exception {
        // given
        doNothing().when(basketService).removeBasket(anyInt(), anyInt());

        // when
        // then
        mvc.perform(delete("/api/basket/{basketId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo(REMOVE_BASKET_SUCCESS.getMessage())));
    }

    @Test
    @DisplayName("사용자의 장바구니 삭제시 장바구니가 없을 경우 에러처리 테스트")
    void testRemoveBasket_ResourceNotFoundException() throws Exception {
        // given
        doThrow(new ResourceNotFoundException("Basket", 1)).when(basketService).removeBasket(anyInt(), anyInt());

        // when
        // then
        mvc.perform(delete("/api/basket/{basketId}", 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Basket 데이터가 존재하지 않습니다 : '1'")));
    }

    @Test
    @DisplayName("사용자의 장바구니 수량 수정 API 테스트")
    void testUpdateQuantity() throws Exception{
        // given
        doNothing().when(basketService).updateBasketQuantity(anyInt(), anyInt(), anyInt());

        // when
        // then
        mvc.perform(put("/api/basket/{basketId}", 1)
                .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo(UPDATE_BASKET_QUANTITY_SUCCESS.getMessage())));
    }

    @Test
    @DisplayName("사용자의 장바구니 수량 수정시 장바구니가 존재하지 않을 경우 에러처리 테스트")
    void testUpdateQuantity_ResourceNotFoundException() throws Exception{
        // given
        doThrow(new ResourceNotFoundException("Basket", 1)).when(basketService).updateBasketQuantity(anyInt(), anyInt(), anyInt());

        // when
        // then
        mvc.perform(put("/api/basket/{basketId}", 1)
                        .param("quantity", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)))
                .andExpect(jsonPath("$.message", equalTo("Basket 데이터가 존재하지 않습니다 : '1'")));
    }
}