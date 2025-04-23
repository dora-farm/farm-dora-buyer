package com.farmdora.farmdorabuyer.basket.controller;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.ADD_BASKET_SUCCESS;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.farmdora.farmdorabuyer.basket.dto.BasketRequestDto;
import com.farmdora.farmdorabuyer.basket.service.BasketService;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}