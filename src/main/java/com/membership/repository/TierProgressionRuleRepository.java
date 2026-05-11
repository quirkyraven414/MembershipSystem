package com.membership.repository;

import com.membership.model.TierProgressionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierProgressionRuleRepository extends JpaRepository<TierProgressionRule, Long> {
    List<TierProgressionRule> findByActiveTrueOrderByPriorityAsc();
    List<TierProgressionRule> findByTargetTierAndActiveTrue(String targetTier);
    Optional<TierProgressionRule> findByRuleName(String ruleName);
}
