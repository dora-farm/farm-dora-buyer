package com.farmdora.farmdorabuyer.user.repository;

import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.user.dto.OrderStatusDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDashboardRepository extends JpaRepository<User, Integer> {

    @Query("SELECT SUM(oo.price) FROM OrderOption oo JOIN oo.order o WHERE o.user.userId = :userId")
    Long sumTotalAmount(@Param("userId") Integer userId);

    @Query("SELECT COUNT(r.id) FROM Review r JOIN r.order o WHERE o.user.userId = :userId")
    Long countReviewsByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.user.userId = :userId")
    Long countInquiriesByUserId(@Param("userId") Integer userId);

    @Query("SELECT new com.farmdora.farmdorabuyer.user.dto.OrderStatusDTO( " +
            "CAST(CASE WHEN o.status.id IN (5, 6) THEN 5 ELSE o.status.id END AS short), " +
            "COUNT(o.id)) " +
            "FROM Order o " +
            "WHERE o.user.userId = :userId AND o.createdDate BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(CASE WHEN o.status.id IN (5, 6) THEN 5 ELSE o.status.id END AS short) " +
            "ORDER BY CAST(CASE WHEN o.status.id IN (5, 6) THEN 5 ELSE o.status.id END AS short)")
    List<OrderStatusDTO> findOrderStatusByUserId(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value =
            "SELECT s.sale_id, s.title, o.name, o.price, sf.save_file " +
                    "FROM `like` l " +
                    "JOIN sale s ON l.sale_id = s.sale_id " +
                    "JOIN `option` o ON s.sale_id = o.sale_id AND o.option_id = (" +
                    "SELECT MIN(option_id) " +
                    "FROM `option` " +
                    "WHERE sale_id = s.sale_id) " +
                    "JOIN sale_file sf ON s.sale_id = sf.sale_id AND sf.is_main = 0 " +
                    "WHERE l.user_id = :userId",
            nativeQuery = true)
    List<Object[]> findWishlistByUserId(@Param("userId") Integer userId);


}
