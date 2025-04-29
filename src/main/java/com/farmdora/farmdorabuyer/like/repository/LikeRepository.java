package com.farmdora.farmdorabuyer.like.repository;

import com.farmdora.farmdorabuyer.entity.Like;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUserAndSale(User user, Sale sale);
}
