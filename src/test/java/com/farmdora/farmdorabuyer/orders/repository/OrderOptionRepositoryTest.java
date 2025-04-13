package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.config.AuditConfig;
import com.farmdora.farmdorabuyer.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(AuditConfig.class)
@DataJpaTest
class OrderOptionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OrderOptionRepository orderOptionRepository;

    @Test
    @DisplayName("주문한 상품의 옵션 조회")
    public void selectOption() {
        // given
        User user = User.builder()
                .build();
        testEntityManager.persist(user);

        OrderStatus orderStatus = OrderStatus.builder()
                .id((short) 2) // 배송중
                .build();
        testEntityManager.persist(orderStatus);

        Order order = Order.builder()
                .user(user)
                .status(orderStatus)
                .build();
        testEntityManager.persist(order);
        LocalDateTime orderTime = order.getCreatedDate();

        Sale sale = Sale.builder()
                .title("제주 삼다수")
                .build();
        testEntityManager.persist(sale);

        SaleFile saleFile = SaleFile.builder()
                .sale(sale)
                .saveFile("sample_image.jpg")
                .isMain(true)
                .build();
        testEntityManager.persist(saleFile);

        Option option1 = Option.builder()
                .sale(sale)
                .name("500ml")
                .price(1000)
                .build();
        Option option2 = Option.builder()
                .sale(sale)
                .name("1L")
                .price(2000)
                .build();
        testEntityManager.persist(option1);
        testEntityManager.persist(option2);

        OrderOption orderOption1 = OrderOption.builder()
                .order(order)
                .option(option1)
                .quantity(3)
                .price(option1.getPrice() * 3)
                .build();
        testEntityManager.persist(orderOption1);

        OrderOption orderOption2 = OrderOption.builder()
                .order(order)
                .option(option2)
                .quantity(5)
                .price(option2.getPrice() * 5)
                .build();
        testEntityManager.persist(orderOption2);

        PayStatus payStatus = PayStatus.builder()
                .id((short) 2) // 결제완료
                .build();
        testEntityManager.persist(payStatus);

        Pay pay = Pay.builder()
                .order(order)
                .status(payStatus)
                .amount(orderOption1.getPrice() + orderOption2.getPrice())
                .build();
        testEntityManager.persist(pay);

        testEntityManager.flush();

        System.out.println("상품 제목 : " + sale.getTitle());
        System.out.println("주문 시간 : " + order.getCreatedDate());
        System.out.println("상품 옵션 1 : " + option1.getName() + "개 " + orderOption1.getQuantity() + " " + orderOption1.getPrice() + "원");
        System.out.println("상품 옵션 2 : " + option2.getName() + "개 " + orderOption2.getQuantity() + " " + orderOption2.getPrice() + "원");
        System.out.println("총 결제금액 : " + pay.getAmount() + "원 결제");
        System.out.println("결제 상태 : " + payStatus.getId());
        System.out.println("배송 상태 : " + orderStatus.getId());
        System.out.println("메인 사진 : " + saleFile.getSaveFile());

        // when
        // List<Order>를 전달하도록 수정
        List<OrderOption> orderOptions = orderOptionRepository.findAllByOrderIn(List.of(order));

        //then
        assertThat(orderOptions).hasSize(2);
        OrderOption foundOrderOption1 = orderOptions.get(0);
        Option foundOptionName1 = foundOrderOption1.getOption();
        assertThat(foundOptionName1.getName()).isEqualTo("500ml");
        assertThat(foundOrderOption1.getQuantity()).isEqualTo(3);
        assertThat(foundOrderOption1.getPrice()).isEqualTo(3000);

        OrderOption foundOrderOption2 = orderOptions.get(1);
        Option foundOptionName2 = foundOrderOption2.getOption();
        assertThat(foundOptionName2.getName()).isEqualTo("1L");
        assertThat(foundOrderOption2.getQuantity()).isEqualTo(5);
        assertThat(foundOrderOption2.getPrice()).isEqualTo(10000);

        Sale foundSale1 = foundOptionName1.getSale();
        Sale foundSale2 = foundOptionName2.getSale();
        assertThat(foundSale1.getTitle()).isEqualTo("제주 삼다수");
        assertThat(foundSale2.getTitle()).isEqualTo("제주 삼다수");

        assertThat(order.getCreatedDate()).isEqualTo(orderTime);
        assertThat(pay.getAmount()).isEqualTo(13000);
        assertThat(payStatus.getId()).isEqualTo((short) 2);
        assertThat(orderStatus.getId()).isEqualTo((short) 2);
        assertThat(saleFile.getSaveFile()).isEqualTo("sample_image.jpg");
    }
}