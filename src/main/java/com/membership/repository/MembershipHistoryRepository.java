package com.membership.repository;

import com.membership.model.MembershipHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipHistoryRepository extends JpaRepository<MembershipHistory, Long> {
    List<MembershipHistory> findByUserIdOrderByActionDateDesc(Long userId);
    
    default List<MembershipHistory> findByUserId(Long userId) {
        return findByUserIdOrderByActionDateDesc(userId);
    }
}

