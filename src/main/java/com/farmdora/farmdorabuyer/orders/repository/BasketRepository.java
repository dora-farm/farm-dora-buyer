package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
    @EntityGraph(attributePaths = {"option"})
    List<Basket> findAllByIdIn(List<Integer> basketIds);

    Optional<Basket> findByUserAndOption(User user, Option option);

    @EntityGraph(attributePaths = {"option", "option.sale"})
    List<Basket> findAllByUser(User user);


    Optional<Basket> findByIdAndUser(Integer basketId, User user);

    Long countByUser(User user);
}
