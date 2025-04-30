package com.farmdora.farmdorabuyer.like.controller;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.ADD_LIKE_SUCCESS;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.farmdora.farmdorabuyer.like.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @MockitoBean
    private LikeService likeService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("찜 추가/삭제 API 테스트")
    void testUpdateLike() throws Exception {
        // given
        doNothing().when(likeService).updateLike(anyInt(), anyInt());

        // when
        // then
        mvc.perform(put("/api/like/{saleId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo(ADD_LIKE_SUCCESS.getMessage())));

        verify(likeService, times(1)).updateLike(anyInt(), anyInt());
    }
}