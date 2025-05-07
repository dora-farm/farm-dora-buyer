package com.farmdora.farmdorabuyer.orders.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.farmdora.farmdorabuyer.entity.Address;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Depot;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.OrderStatus;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromBasketDTO;
import com.farmdora.farmdorabuyer.orders.repository.BasketRepository;
import com.farmdora.farmdorabuyer.orders.repository.DepotRepository;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderStatusRepository;
import com.farmdora.farmdorabuyer.orders.repository.SaleRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@SpringBootTest
@TestExecutionListeners(
        value = TransactionalTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@ActiveProfiles("test")
public class PurchaseConcurrencyTest {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepotRepository depotRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    private Integer userId;
    private Integer optionId;
    private Integer depotId;
    private List<Integer> basketIds;

    @BeforeEach
    void setup() {
        User user = userRepository.save(User.builder().email("test@test.com").build());
        userId = user.getUserId();

        Address address = Address.builder()
                .addr("서울시")
                .detailAddr("강남구")
                .postNum("1234")
                .build();
        Depot depot = depotRepository.save(Depot.builder()
                .address(address)
                .user(user)
                .name("depot")
                .deliveryName("강남구")
                .build());
        depotId = depot.getId();

        Sale sale = new Sale();
        saleRepository.save(sale);

        Option option = Option.builder()
                .quantity(10)
                .price(1000)
                .name("테스트 옵션")
                .sale(sale)
                .build();
        optionRepository.save(option);
        optionId = option.getId();

        basketIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Basket basket = Basket.builder()
                    .option(option)
                    .quantity(1)
                    .user(user)
                    .build();
            basketRepository.save(basket);
            basketIds.add(basket.getId());
        }

        orderStatusRepository.save(OrderStatus.builder()
                .id(Short.valueOf("1"))
                .name("배송준비")
                .build());
    }

    @Test
    void testPurchase() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            int basketId = basketIds.get(i);
            executorService.submit(() -> {
                try {
                    OrderFromBasketDTO orderRequest = OrderFromBasketDTO.builder()
                            .depotId(depotId)
                            .basketIds(List.of(basketId))
                            .build();

                    purchaseService.orderFromBaskets(userId, orderRequest);
                } catch (Exception e) {
                    System.err.println("에러: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Option updatedOption = optionRepository.findById(optionId).orElseThrow();
        System.out.println("최종 재고 = " + updatedOption.getQuantity());
        assertThat(updatedOption.getQuantity()).isEqualTo(0);
    }
}
