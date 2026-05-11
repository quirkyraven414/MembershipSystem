package com.membership.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long membershipId;
    
    private Long userId;
    private String tier;
    private String duration;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private double basePrice;
    private double paidPrice;
    private String status;
    private boolean hasDowngraded;
    private LocalDateTime downgradeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Membership() {}

    public Membership(Long userId, String tier, String duration, LocalDateTime startDate, 
                     LocalDateTime expiryDate, double basePrice, double paidPrice) {
        this.userId = userId;
        this.tier = tier;
        this.duration = duration;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.basePrice = basePrice;
        this.paidPrice = paidPrice;
        this.status = "ACTIVE";
        this.hasDowngraded = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getMembershipId() { return membershipId; }
    public void setMembershipId(Long membershipId) { this.membershipId = membershipId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public double getPaidPrice() { return paidPrice; }
    public void setPaidPrice(double paidPrice) { this.paidPrice = paidPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean hasDowngraded() { return hasDowngraded; }
    public void setHasDowngraded(boolean hasDowngraded) { this.hasDowngraded = hasDowngraded; }

    public LocalDateTime getDowngradeDate() { return downgradeDate; }
    public void setDowngradeDate(LocalDateTime downgradeDate) { this.downgradeDate = downgradeDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
