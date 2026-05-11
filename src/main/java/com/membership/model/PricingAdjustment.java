package com.membership.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_adjustments")
public class PricingAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adjustmentId;
    
    private String name;
    private String type;
    
    @Column(name = "adjustment_value")
    private double value;
    
    private boolean isActive;
    private int priority;
    private LocalDateTime createdAt;

    public PricingAdjustment() {}

    public PricingAdjustment(String name, String type, double value, boolean isActive, int priority) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.isActive = isActive;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
    }

    public Long getAdjustmentId() { return adjustmentId; }
    public void setAdjustmentId(Long adjustmentId) { this.adjustmentId = adjustmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
