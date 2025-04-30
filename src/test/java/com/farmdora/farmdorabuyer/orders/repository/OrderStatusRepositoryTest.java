package com.farmdora.farmdorabuyer.orders.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.farmdora.farmdorabuyer.entity.OrderStatus;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class OrderStatusRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Test
    @DisplayName("findByName() 쿼리메서드 테스트")
    void testFindByName() {
        // given
        String statusName = "배송준비";

        OrderStatus status = OrderStatus.builder()
                .id((short) 1)
                .name(statusName)
                .build();
        em.persist(status);

        em.flush();
        em.clear();

        // when
        Optional<OrderStatus> orderStatus = orderStatusRepository.findByName(statusName);

        // then
        assertThat(orderStatus.isPresent()).isEqualTo(true);
        assertThat(orderStatus.get().getName()).isEqualTo(statusName);
    }
}