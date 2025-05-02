package com.farmdora.farmdorabuyer.like.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.farmdora.farmdorabuyer.entity.Like;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class LikeRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private LikeRepository likeRepository;

    @Test
    @DisplayName("findByUserAndSale() 쿼리메서드 테스트")
    void testFindByUserAndSale() {
        // given
        User user = new User();
        em.persist(user);

        Sale sale = new Sale();
        em.persist(sale);

        Like like = Like.builder()
                .user(user)
                .sale(sale)
                .build();
        em.persist(like);

        em.flush();
        em.clear();

        // when
        Optional<Like> savedLike = likeRepository.findByUserAndSale(user, sale);

        // then
        assertThat(savedLike.isPresent()).isEqualTo(true);
    }
}