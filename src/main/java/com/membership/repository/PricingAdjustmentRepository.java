package com.membership.repository;

import com.membership.model.PricingAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingAdjustmentRepository extends JpaRepository<PricingAdjustment, Long> {
    List<PricingAdjustment> findByIsActiveTrueAndTypeOrderByPriority(String type);
    List<PricingAdjustment> findByIsActiveTrueOrderByPriority();
    
    default List<PricingAdjustment> findActiveByType(String type) {
        return findByIsActiveTrueAndTypeOrderByPriority(type);
    }
    
    default List<PricingAdjustment> findAllActive() {
        return findByIsActiveTrueOrderByPriority();
    }
}

