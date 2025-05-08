package com.farmdora.farmdorabuyer.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Address;
import com.farmdora.farmdorabuyer.entity.BankType;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Depot;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.Order;
import com.farmdora.farmdorabuyer.entity.OrderOption;
import com.farmdora.farmdorabuyer.entity.OrderStatus;
import com.farmdora.farmdorabuyer.entity.Pay;
import com.farmdora.farmdorabuyer.entity.PayStatus;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromBasketDTO;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromOptionDTO;
import com.farmdora.farmdorabuyer.orders.exception.NotUserOfDepotException;
import com.farmdora.farmdorabuyer.orders.exception.OutOfStockException;
import com.farmdora.farmdorabuyer.orders.repository.BasketRepository;
import com.farmdora.farmdorabuyer.orders.repository.DepotRepository;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderOptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderStatusRepository;
import com.farmdora.farmdorabuyer.orders.repository.PayRepository;
import com.farmdora.farmdorabuyer.orders.repository.PayStatusRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderOptionRepository orderOptionRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private DepotRepository depotRepository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private OptionRepository optionRepository;

    @Mock
    private PayRepository payRepository;

    @Mock
    private PayStatusRepository payStatusRepository;

    @Mock
    private EntityManager em;

    @InjectMocks
    private PurchaseService purchaseService;

    @Nested
    @DisplayName("장바구니로 주문 서비스 레이어 테스트")
    class OrderFromBasketsTests {

        private static final OrderFromBasketDTO orderRequest = OrderFromBasketDTO.builder()
                .basketIds(List.of(1, 2))
                .depotId(1)
                .build();

        @Test
        @DisplayName("주문 성공")
        void testOrder() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .bankType(BankType.builder()
                            .name("신한은행")
                            .build())
                    .build();
            when(userRepository.findByUserIdWithBankType(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(mockUser)
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            Sale mockSale1 = new Sale();
            Option mockOption1 = Option.builder()
                    .id(1)
                    .quantity(10)
                    .sale(mockSale1)
                    .price(10000)
                    .build();
            Sale mockSale2 = new Sale();
            Option mockOption2 = Option.builder()
                    .id(2)
                    .quantity(10)
                    .sale(mockSale2)
                    .price(20000)
                    .build();
            List<Basket> baskets = List.of(
                    Basket.builder()
                            .id(1)
                            .user(mockUser)
                            .option(mockOption1)
                            .quantity(3)
                            .build(),
                    Basket.builder()
                            .id(2)
                            .user(mockUser)
                            .option(mockOption2)
                            .quantity(5)
                            .build()
            );
            when(basketRepository.findAllByIdIn(anyList())).thenReturn(baskets);

            OrderStatus mockOrderStatus = OrderStatus.builder()
                    .name("배송준비")
                    .build();
            when(orderStatusRepository.findByName(anyString())).thenReturn(Optional.of(mockOrderStatus));

            when(optionRepository.findByIdForUpdate(1)).thenReturn(Optional.of(mockOption1));
            when(optionRepository.findByIdForUpdate(2)).thenReturn(Optional.of(mockOption2));

            PayStatus mockPayStatus = PayStatus.builder()
                    .id(Short.valueOf("1"))
                    .name("결제완료")
                    .build();
            when(payStatusRepository.findByName("결제완료")).thenReturn(Optional.of(mockPayStatus));

            // when
            purchaseService.orderFromBaskets(1, orderRequest);

            // then
            verify(orderRepository, times(2)).save(any(Order.class));
            verify(orderOptionRepository, times(2)).save(any(OrderOption.class));
            verify(payRepository, times(2)).save(any(Pay.class));
            assertThat(baskets.get(0).getOption().getQuantity()).isEqualTo(7);
            assertThat(baskets.get(1).getOption().getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("주문시 사용자가 존재하지 않을 경우 예외발생 테스트")
        void testOrder_UserNotFoundException() {
            // given
            when(userRepository.findByUserIdWithBankType(anyInt())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromBaskets(1, new OrderFromBasketDTO()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("주문시 배송지가 존재하지 않을 경우 예외발생 테스트")
        void testOrder_DepotNotFoundException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findByUserIdWithBankType(anyInt())).thenReturn(Optional.of(mockUser));
            when(depotRepository.findById(anyInt())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromBaskets(1, orderRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("주문시 조회한 배송지가 사용자의 배송지가 아닐 경우 예외발생 테스트")
        void testOrder_NotDepotOfUserException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findByUserIdWithBankType(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(User.builder().userId(2).build())
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromBaskets(1, orderRequest))
                    .isInstanceOf(NotUserOfDepotException.class);
        }

        @Test
        @DisplayName("주문시 설정할 배송상태가 존재하지 않을 경우 예외발생 테스트")
        void testOrder_OrderStatusNotFoundException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findByUserIdWithBankType(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(mockUser)
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            Sale mockSale1 = new Sale();
            Sale mockSale2 = new Sale();
            List<Basket> baskets = List.of(
                    Basket.builder()
                            .id(1)
                            .user(mockUser)
                            .option(Option.builder()
                                    .quantity(10)
                                    .sale(mockSale1)
                                    .price(10000)
                                    .build())
                            .quantity(3)
                            .build(),
                    Basket.builder()
                            .id(2)
                            .user(mockUser)
                            .option(Option.builder()
                                    .quantity(10)
                                    .sale(mockSale2)
                                    .price(20000)
                                    .build())
                            .quantity(5)
                            .build()
            );
            when(basketRepository.findAllByIdIn(anyList())).thenReturn(baskets);

            when(orderStatusRepository.findByName(anyString())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromBaskets(1, orderRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("주문시 주문한 수량이 옵션의 재고를 초과할 경우 예외발생 테스트")
        void testOrder_OutOfStockException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findByUserIdWithBankType(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(mockUser)
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            Sale mockSale1 = new Sale();
            Option mockOption1 = Option.builder()
                    .id(1)
                    .quantity(1)
                    .sale(mockSale1)
                    .price(10000)
                    .build();
            Sale mockSale2 = new Sale();
            Option mockOption2 = Option.builder()
                    .id(2)
                    .quantity(10)
                    .sale(mockSale2)
                    .price(20000)
                    .build();
            List<Basket> baskets = List.of(
                    Basket.builder()
                            .id(1)
                            .user(mockUser)
                            .option(mockOption1)
                            .quantity(3)
                            .build(),
                    Basket.builder()
                            .id(2)
                            .user(mockUser)
                            .option(mockOption2)
                            .quantity(5)
                            .build()
            );
            when(basketRepository.findAllByIdIn(anyList())).thenReturn(baskets);

            OrderStatus mockOrderStatus = OrderStatus.builder()
                    .name("배송준비")
                    .build();
            when(orderStatusRepository.findByName(anyString())).thenReturn(Optional.of(mockOrderStatus));

            when(optionRepository.findByIdForUpdate(anyInt())).thenReturn(Optional.of(mockOption1));

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromBaskets(1, orderRequest))
                    .isInstanceOf(OutOfStockException.class);
        }
    }

    @Nested
    @DisplayName("옵션으로 주문 서비스 레이어 테스트")
    class OrderFromOptionTests {

        private static final OrderFromOptionDTO orderRequest = OrderFromOptionDTO.builder()
                .optionId(1)
                .depotId(1)
                .quantity(1)
                .build();

        @Test
        @DisplayName("주문 성공")
        void testOrder() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(mockUser)
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            OrderStatus mockOrderStatus = OrderStatus.builder()
                    .name("배송준비")
                    .build();
            when(orderStatusRepository.findByName(anyString())).thenReturn(Optional.of(mockOrderStatus));

            Option mockOption = Option.builder()
                    .id(1)
                    .price(10000)
                    .quantity(10)
                    .build();
            when(optionRepository.findByIdForUpdate(anyInt())).thenReturn(Optional.of(mockOption));

            // when
            purchaseService.orderFromOption(1, orderRequest);

            // then
            verify(orderRepository, times(1)).save(any(Order.class));
            verify(orderOptionRepository, times(1)).save(any(OrderOption.class));
            assertThat(mockOption.getQuantity()).isEqualTo(9);
        }

        @Test
        @DisplayName("주문시 사용자가 존재하지 않을 경우 예외발생 테스트")
        void testOrder_UserNotFoundException() {
            // given
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromOption(1, new OrderFromOptionDTO()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("주문시 배송지가 존재하지 않을 경우 예외발생 테스트")
        void testOrder_DepotNotFoundException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));
            when(depotRepository.findById(anyInt())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromOption(1, orderRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("주문시 조회한 배송지가 사용자의 배송지가 아닐 경우 예외발생 테스트")
        void testOrder_NotDepotOfUserException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(User.builder().userId(2).build())
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromOption(1, orderRequest))
                    .isInstanceOf(NotUserOfDepotException.class);
        }

        @Test
        @DisplayName("주문시 설정할 배송상태가 존재하지 않을 경우 예외발생 테스트")
        void testOrder_OrderStatusNotFoundException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(mockUser)
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            when(orderStatusRepository.findByName(anyString())).thenReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> purchaseService.orderFromOption(1, orderRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("주문시 주문한 수량이 옵션의 재고를 초과할 경우 예외발생 테스트")
        void testOrder_OutOfStockException() {
            // given
            User mockUser = User.builder()
                    .userId(1)
                    .build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

            Depot depot = Depot.builder()
                    .id(1)
                    .user(mockUser)
                    .address(new Address())
                    .build();
            when(depotRepository.findById(anyInt())).thenReturn(Optional.of(depot));

            OrderStatus mockOrderStatus = OrderStatus.builder()
                    .name("배송준비")
                    .build();
            when(orderStatusRepository.findByName(anyString())).thenReturn(Optional.of(mockOrderStatus));

            Option mockOption = Option.builder()
                    .id(1)
                    .price(10000)
                    .quantity(10)
                    .build();
            when(optionRepository.findByIdForUpdate(anyInt())).thenReturn(Optional.of(mockOption));

            // when
            // then
            OrderFromOptionDTO orderRequest = OrderFromOptionDTO.builder()
                    .depotId(1)
                    .optionId(1)
                    .quantity(11)
                    .build();
            assertThatThrownBy(() -> purchaseService.orderFromOption(1, orderRequest))
                    .isInstanceOf(OutOfStockException.class);
        }
    }
}