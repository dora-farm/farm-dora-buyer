package com.farmdora.farmdorabuyer.popup.controller;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.farmdora.farmdorabuyer.common.response.SuccessMessage;
import com.farmdora.farmdorabuyer.popup.dto.PopupDTO;
import com.farmdora.farmdorabuyer.popup.service.PopupService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PopupController.class)
class PopupControllerTest {

    @MockitoBean
    private PopupService popupService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("팝업 목록 조회 ")
    void testGetPopups() throws Exception {
        // given
        List<PopupDTO> popups = List.of(
                PopupDTO.builder()
                        .id(1)
                        .imageUrl("imageUrl")
                        .build(),
                PopupDTO.builder()
                        .id(2)
                        .imageUrl("imageUrl")
                        .build()
        );
        when(popupService.getPopups()).thenReturn(popups);

        // when
        // then
        mvc.perform(get("/api/popup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.message", equalTo(SuccessMessage.GET_POPUPS_SUCCESS.getMessage())));
    }
}