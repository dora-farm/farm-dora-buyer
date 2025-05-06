package com.farmdora.farmdorabuyer;

import com.farmdora.farmdorabuyer.basket.controller.BasketController;
import com.farmdora.farmdorabuyer.basket.service.BasketService;
import com.farmdora.farmdorabuyer.like.controller.LikeController;
import com.farmdora.farmdorabuyer.like.service.LikeService;
import com.farmdora.farmdorabuyer.orders.controller.PurchaseController;
import com.farmdora.farmdorabuyer.orders.service.PurchaseService;
import com.farmdora.farmdorabuyer.popup.controller.PopupController;
import com.farmdora.farmdorabuyer.popup.service.PopupService;
import com.farmdora.farmdorabuyer.security.TestSecurityConfig;
import com.farmdora.farmdorabuyer.security.WithCustomMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    BasketController.class,
    LikeController.class,
    PurchaseController.class,
    PopupController.class
})
@WithCustomMockUser
@Import({TestSecurityConfig.class})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected BasketService basketService;

    @MockitoBean
    protected LikeService likeService;

    @MockitoBean
    protected PurchaseService purchaseService;

    @MockitoBean
    protected PopupService popupService;

}
