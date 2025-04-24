package com.farmdora.farmdorabuyer.basket.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import java.util.List;
import java.util.Optional;
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
    @DisplayName("findByUserAndOption() 쿼리메서드 테스트")
    void testFindByUserAndOption() {
        // given
        User user = new User();
        em.persist(user);

        Option option = new Option();
        em.persist(option);

        Basket basket = Basket.builder()
                .user(user)
                .option(option)
                .quantity(2)
                .build();
        em.persist(basket);

        em.flush();
        em.clear();

        // when
        Optional<Basket> savedBasket = basketRepository.findByUserAndOption(user, option);

        // then
        assertThat(savedBasket.isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("findAllByUser() 쿼리메서드 테스트")
    void testFindAllByUser() {
        // given
        User user = new User();
        em.persist(user);

        Option option = Option.builder()
                .name("옵션")
                .build();
        em.persist(option);

        Basket basket1 = Basket.builder()
                .user(user)
                .option(option)
                .quantity(2)
                .build();
        Basket basket2 = Basket.builder()
                .user(user)
                .option(option)
                .quantity(4)
                .build();
        em.persist(basket1);
        em.persist(basket2);

        // when
        List<Basket> baskets = basketRepository.findAllByUser(user);

        // then
        assertThat(baskets.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("findByIdAndUser() 쿼리메서드 테스트")
    void testFindByIdAndUser() {
        // given
        User user = new User();
        em.persist(user);

        Option option = Option.builder()
                .name("옵션")
                .build();
        em.persist(option);

        Basket basket = Basket.builder()
                .user(user)
                .option(option)
                .quantity(2)
                .build();
        em.persist(basket);

        em.flush();
        em.clear();

        // when
        Optional<Basket> savedBasket = basketRepository.findByIdAndUser(basket.getId(), user);

        // then
        assertThat(savedBasket.isPresent()).isEqualTo(true);
        assertThat(savedBasket.get().getQuantity()).isEqualTo(2);
    }
}