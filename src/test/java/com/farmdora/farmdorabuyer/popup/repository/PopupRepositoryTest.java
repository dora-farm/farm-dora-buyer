package com.farmdora.farmdorabuyer.popup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.farmdora.farmdorabuyer.config.AuditConfig;
import com.farmdora.farmdorabuyer.entity.Popup;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(AuditConfig.class)
class PopupRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PopupRepository popupRepository;

    @Test
    @DisplayName("findByEndDateGreaterThanEqual() 쿼리메서드 테스트")
    void testFindByEndDateGreaterThanEqual() {
        // given
        Popup popup1 = Popup.builder()
                .startDate(LocalDateTime.now().minusDays(7))
                .endDate(LocalDateTime.now().plusDays(7))
                .build();
        em.persist(popup1);

        Popup popup2 = Popup.builder()
                .startDate(LocalDateTime.now().minusDays(7))
                .endDate(LocalDateTime.now().minusDays(1))
                .build();
        em.persist(popup2);

        em.flush();
        em.clear();

        // when
        List<Popup> popups = popupRepository.findByEndDateGreaterThanEqual(LocalDateTime.now());

        // then
        assertThat(popups.size()).isEqualTo(1);
    }
}