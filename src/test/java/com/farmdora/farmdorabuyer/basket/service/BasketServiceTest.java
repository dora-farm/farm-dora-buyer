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
import com.farmdora.farmdorabuyer.basket.exception.QuantityOverLimitException;
import com.farmdora.farmdorabuyer.basket.exception.BasketOverLimitException;
import com.farmdora.farmdorabuyer.common.exception.ResourceAlreadyExistsException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.common.response.PageResponseDTO;
import com.farmdora.farmdorabuyer.common.util.NcpImageProperties;
import com.farmdora.farmdorabuyer.common.util.NcpImageProperties.ImageInfo;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.repository.BasketRepository;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NcpImageProperties ncpImageProperties;

    @InjectMocks
    private BasketService basketService;

    @Nested
    @DisplayName("장바구니 추가 서비스 레이어 테스트")
    class AddBasketTests {

        @Test
        @DisplayName("장바구니 추가 성공")
        void testAddBasket() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Option mockOption = Option.builder()
                    .name("옵션")
                    .quantity(20)
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
        @DisplayName("장바구니 추가시 존재하지 않는 옵션일 경우 예외 발생")
        void testAddBasket_QuantityOverLimitException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Option mockOption = Option.builder()
                    .name("옵션")
                    .quantity(1)
                    .build();
            when(optionRepository.findById(anyInt())).thenReturn(Optional.of(mockOption));

            // when
            // then
            BasketRequestDto basketAddRequest = BasketRequestDto.builder()
                    .optionId(1)
                    .quantity(10)
                    .build();
            assertThatThrownBy(() -> basketService.addBasket(1, basketAddRequest))
                    .isInstanceOf(QuantityOverLimitException.class);
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
                    .quantity(20)
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
                    .quantity(20)
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
    }

    @Nested
    @DisplayName("장바구니 목록 조회 서비스 레이어 테스트")
    class GetBasketsTests {

        @Test
        @DisplayName("사용자의 장바구니 목록 조회 성공")
        void testGetBaskets() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            List<BasketResponseDto> baskets = List.of(
                    BasketResponseDto.builder()
                            .basketId(1)
                            .saleId(1)
                            .title("title1")
                            .option("option1")
                            .quantity(10)
                            .price(10000)
                            .imageUrl("imageUrl1")
                            .build(),
                    BasketResponseDto.builder()
                            .basketId(2)
                            .saleId(2)
                            .title("title2")
                            .option("option2")
                            .quantity(20)
                            .price(20000)
                            .imageUrl("imageUrl2")
                            .build()
            );
            Pageable pageable = PageRequest.of(0, 5);
            Page<BasketResponseDto> basketPage = new PageImpl<>(baskets, pageable, 2);
            when(basketRepository.findAllWithMainImageByUser(any(User.class), any(Pageable.class))).thenReturn(basketPage);

            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setPath("path");
            imageInfo.setType("type");
            when(ncpImageProperties.getProduct()).thenReturn(imageInfo);

            // when
            PageResponseDTO<BasketResponseDto> result = basketService.getBaskets(1, PageRequest.of(0, 10));

            // then
            BasketResponseDto basket1 = result.getContents().get(0);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(basket1.getTitle()).isEqualTo("title1");
            assertThat(basket1.getQuantity()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("장바구니 삭제 서비스 레이어 테스트")
    class RemoveBasketTests {

        @Test
        @DisplayName("사용자의 특정 장바구니 삭제 성공")
        void testRemoveBasket() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Basket mockBasket = Basket.builder()
                    .id(1)
                    .user(mockUser)
                    .build();
            when(basketRepository.findByIdAndUser(anyInt(), any(User.class))).thenReturn(Optional.of(mockBasket));

            // when
            basketService.removeBasket(1, 1);

            // then
            verify(basketRepository, times(1)).delete(any(Basket.class));
        }

        @Test
        @DisplayName("사용자의 특정 장바구니 삭제시 장바구니가 존재하지 않을 경우 예외 발생 테스트")
        void testRemoveBasket_ResourceNotFoundException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            when(basketRepository.findByIdAndUser(anyInt(), any(User.class))).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> basketService.removeBasket(1, 1))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("장바구니 추가 서비스 레이어 테스트")
    class UpdateBasketTests {

        @Test
        @DisplayName("사용자의 장바구니 수량 수정 성공")
        void testUpdateBasketQuantity() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Basket mockBasket = Basket.builder()
                    .id(1)
                    .user(mockUser)
                    .quantity(1)
                    .build();
            when(basketRepository.findByIdAndUser(anyInt(), any(User.class))).thenReturn(Optional.of(mockBasket));

            // when
            basketService.updateBasketQuantity(1, 1, 10);

            // then
            assertThat(mockBasket.getQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("사용자의 장바구니 수량 수정시 장바구니가 존재하지 않을 경우 예외 발생 테스트")
        void testUpdateBasketQuantity_ResourceNotFoundException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            when(basketRepository.findByIdAndUser(anyInt(), any(User.class))).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> basketService.updateBasketQuantity(1, 1, 10))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}