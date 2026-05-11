package com.membership.repository;

import com.membership.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(o.price), 0) FROM Order o WHERE o.userId = :userId")
    Double sumTotalValueByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(o.price), 0) FROM Order o WHERE o.userId = :userId AND o.orderDate >= :startDate")
    Double sumValueByUserIdAndDateAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.orderDate >= :startDate")
    Long countByUserIdAndDateAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
