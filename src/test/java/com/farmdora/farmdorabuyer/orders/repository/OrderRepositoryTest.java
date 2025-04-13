package com.farmdora.farmdorabuyer.orders.repository;


import com.farmdora.farmdorabuyer.config.AuditConfig;
import com.farmdora.farmdorabuyer.entity.Order;
import com.farmdora.farmdorabuyer.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Import(AuditConfig.class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("사용자의 기간에 관한 데이터 조회")
    public void whenFindAllByUserUserIdAndCreatedDateBetween_withNoOrders_thenReturnEmptyList() {
        //given
        User user = User.builder()
                .build();
        testEntityManager.persist(user);

        Order order = Order.builder()
                .user(user)
                .build();
        testEntityManager.persist(order);

        LocalDateTime now = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);

        testEntityManager.flush();

        System.out.println("order: " + order.getCreatedDate());

        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Order> orders = orderRepository.findAllByUserUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(
                user.getUserId(), now, tomorrow, pageable);

        //then
        assertThat(orders.getContent().size()).isEqualTo(1);
        assertThat(orders.getTotalElements()).isEqualTo(1);
        assertThat(orders.getTotalPages()).isEqualTo(1);
    }
}