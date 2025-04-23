package com.farmdora.farmdorabuyer.basket.repository;

import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
    Optional<Basket> findByUserAndOption(User user, Option option);
}