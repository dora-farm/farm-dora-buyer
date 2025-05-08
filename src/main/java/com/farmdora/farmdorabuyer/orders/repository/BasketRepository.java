package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.basket.dto.BasketResponseDto;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
    @EntityGraph(attributePaths = {"option"})
    List<Basket> findAllByIdIn(List<Integer> basketIds);

    Optional<Basket> findByUserAndOption(User user, Option option);

    @EntityGraph(attributePaths = {"option", "option.sale"})
    List<Basket> findAllByUser(User user);

    Optional<Basket> findByIdAndUser(Integer basketId, User user);

    Long countByUser(User user);

    @Query("""
        SELECT new com.farmdora.farmdorabuyer.basket.dto.BasketResponseDto(
            b.id,
            s.id,
            s.title,
            o.name,
            o.quantity,
            b.quantity,
            o.price,
            sf.saveFile
        )
        FROM Basket b
        JOIN b.option o
        JOIN o.sale s
        LEFT JOIN SaleFile sf ON sf.sale = s AND sf.isMain = false
        WHERE b.user = :user
    """)
    Page<BasketResponseDto> findAllWithMainImageByUser(@Param("user") User user, Pageable pageable);
}
