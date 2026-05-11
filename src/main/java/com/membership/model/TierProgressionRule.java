package com.membership.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tier_progression_rules")
public class TierProgressionRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleName;

    @Column(nullable = false)
    private String targetTier;

    @Column(nullable = false)
    private String criteriaType;

    private String operator;

    private Double thresholdValue;

    private String timeWindow;

    private String cohortName;

    @Column(nullable = false)
    private boolean active = true;

    private Integer priority;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public TierProgressionRule() {}

    public TierProgressionRule(String ruleName, String targetTier, String criteriaType, 
                              String operator, Double thresholdValue, String timeWindow, 
                              String cohortName, boolean active, Integer priority) {
        this.ruleName = ruleName;
        this.targetTier = targetTier;
        this.criteriaType = criteriaType;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
        this.timeWindow = timeWindow;
        this.cohortName = cohortName;
        this.active = active;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getTargetTier() {
        return targetTier;
    }

    public void setTargetTier(String targetTier) {
        this.targetTier = targetTier;
    }

    public String getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(Double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(String timeWindow) {
        this.timeWindow = timeWindow;
    }

    public String getCohortName() {
        return cohortName;
    }

    public void setCohortName(String cohortName) {
        this.cohortName = cohortName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
