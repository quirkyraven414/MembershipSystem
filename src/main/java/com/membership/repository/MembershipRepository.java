package com.membership.repository;

import com.membership.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByUserIdAndStatus(Long userId, String status);
    List<Membership> findByUserId(Long userId);
    
    default Optional<Membership> findActiveByUserId(Long userId) {
        return findByUserIdAndStatus(userId, "ACTIVE");
    }
}

