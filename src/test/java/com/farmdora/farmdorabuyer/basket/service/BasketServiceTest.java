package com.farmdora.farmdorabuyer.basket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.farmdora.farmdorabuyer.basket.dto.BasketRequestDto;
import com.farmdora.farmdorabuyer.basket.dto.BasketResponseDto;
import com.farmdora.farmdorabuyer.basket.exception.BasketOverLimitException;
import com.farmdora.farmdorabuyer.common.exception.ResourceAlreadyExistsException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.repository.BasketRepository;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasketService basketService;

    @Test
    @DisplayName("장바구니 추가 서비스 레이어 테스트")
    void testAddBasket() {
        // given
        User mockUser = User.builder()
                .userId(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        Option mockOption = Option.builder()
                .name("옵션")
                .build();
        when(optionRepository.findById(anyInt())).thenReturn(Optional.of(mockOption));

        // when
        BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                .optionId(1)
                .quantity(10)
                .build();
        basketService.addBasket(1, basketAddRequest);

        // then
        verify(basketRepository, times(1)).save(any(Basket.class));
    }

    @Test
    @DisplayName("장바구니 추가시 존재하지 않는 옵션일 경우 예외 발생")
    void testAddBasket_OptionNotFoundException() {
        // given
        User mockUser = User.builder()
                .userId(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));
        when(optionRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when
        // then
        BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                .optionId(1)
                .quantity(10)
                .build();
        assertThatThrownBy(() -> basketService.addBasket(1, basketAddRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("장바구니 추가시 이미 존재할 경우 예외 발생")
    void testAddBasket_BasketAlreadyExistsException() {
        // given
        User mockUser = User.builder()
                .userId(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        Option mockOption = Option.builder()
                .name("옵션")
                .build();
        when(optionRepository.findById(anyInt())).thenReturn(Optional.of(mockOption));

        Basket mockBasket = Basket.builder()
                .user(mockUser)
                .option(mockOption)
                .build();
        when(basketRepository.findByUserAndOption(any(User.class), any(Option.class))).thenReturn(Optional.of(mockBasket));

        // when
        // then
        BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                .optionId(1)
                .quantity(10)
                .build();
        assertThatThrownBy(() -> basketService.addBasket(1, basketAddRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("장바구니 추가시 장바구니 목록이 이미 16개일 경우 예외 발생")
    void testAddBasket_BasketOverLimitException() {
        // given
        User mockUser = User.builder()
                .userId(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        Option mockOption = Option.builder()
                .id(1)
                .name("옵션")
                .build();
        when(optionRepository.findById(anyInt())).thenReturn(Optional.of(mockOption));

        when(basketRepository.findByUserAndOption(any(User.class), any(Option.class))).thenReturn(Optional.empty());
        when(basketRepository.countByUser(any(User.class))).thenReturn(16L);

        // when
        // then
        BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                .optionId(1)
                .quantity(10)
                .build();
        assertThatThrownBy(() -> basketService.addBasket(1, basketAddRequest))
                .isInstanceOf(BasketOverLimitException.class);
    }

    @Test
    @DisplayName("사용자의 장바구니 목록 조회")
    void testGetBaskets() {
        // given
        User mockUser = User.builder()
                .userId(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        Sale mockSale = Sale.builder()
                .id(1)
                .title("상품")
                .build();

        Option mockOption = Option.builder()
                .id(1)
                .sale(mockSale)
                .name("옵션")
                .build();

        List<Basket> baskets = List.of(
                Basket.builder()
                        .user(mockUser)
                        .quantity(2)
                        .option(mockOption)
                        .build(),
                Basket.builder()
                        .user(mockUser)
                        .quantity(10)
                        .option(mockOption)
                        .build()
        );
        when(basketRepository.findAllByUser(any(User.class))).thenReturn(baskets);

        // when
        List<BasketResponseDto> result = basketService.getBaskets(1);

        // then
        BasketResponseDto basket1 = result.get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(basket1.getTitle()).isEqualTo("상품");
        assertThat(basket1.getQuantity()).isEqualTo(2);
    }
}