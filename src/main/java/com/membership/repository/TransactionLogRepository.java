package com.membership.repository;

import com.membership.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    List<TransactionLog> findByUserIdOrderByPaymentDateDesc(Long userId);
    
    default List<TransactionLog> findByUserId(Long userId) {
        return findByUserIdOrderByPaymentDateDesc(userId);
    }
}

