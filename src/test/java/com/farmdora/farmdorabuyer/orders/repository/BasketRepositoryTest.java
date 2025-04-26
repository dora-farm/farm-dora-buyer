package com.farmdora.farmdorabuyer.orders.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.farmdora.farmdorabuyer.entity.Basket;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class BasketRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BasketRepository basketRepository;

    @Test
    @DisplayName("findAllByIdIn() 쿼리메서드 테스트")
    void testFindAllByIdIn() {
        // given
        Basket basket1 = Basket.builder()
                .quantity(1)
                .build();
        Basket basket2 = Basket.builder()
                .quantity(2)
                .build();
        em.persist(basket1);
        em.persist(basket2);

        em.flush();
        em.clear();

        // when
        List<Basket> baskets = basketRepository.findAllByIdIn(List.of(basket1.getId(), basket2.getId()));

        // then
        assertThat(baskets.size()).isEqualTo(2);
    }
}