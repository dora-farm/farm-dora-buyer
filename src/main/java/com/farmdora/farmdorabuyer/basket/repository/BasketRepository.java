package com.farmdora.farmdorabuyer.basket.repository;

import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
    Optional<Basket> findByUserAndOption(User user, Option option);

    @EntityGraph(attributePaths = {"option", "option.sale"})
    List<Basket> findAllByUser(User user);

    Long countByUser(User user);
}