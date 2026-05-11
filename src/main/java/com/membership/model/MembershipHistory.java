package com.membership.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_history")
public class MembershipHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    
    private Long userId;
    private Long membershipId;
    private String tier;
    private String duration;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private String action;
    private LocalDateTime actionDate;
    private String fromTier;
    private String toTier;
    private double basePrice;
    private double paidPrice;
    private double creditApplied;
    private double adjustmentAmount;
    
    @Column(length = 500)
    private String adjustmentReason;

    public MembershipHistory() {}

    public MembershipHistory(Long userId, Long membershipId, String tier, String duration,
                           LocalDateTime startDate, LocalDateTime expiryDate, String action,
                           String fromTier, String toTier, double basePrice, double paidPrice,
                           double creditApplied, double adjustmentAmount, String adjustmentReason) {
        this.userId = userId;
        this.membershipId = membershipId;
        this.tier = tier;
        this.duration = duration;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.action = action;
        this.actionDate = LocalDateTime.now();
        this.fromTier = fromTier;
        this.toTier = toTier;
        this.basePrice = basePrice;
        this.paidPrice = paidPrice;
        this.creditApplied = creditApplied;
        this.adjustmentAmount = adjustmentAmount;
        this.adjustmentReason = adjustmentReason;
    }

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMembershipId() { return membershipId; }
    public void setMembershipId(Long membershipId) { this.membershipId = membershipId; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }

    public String getFromTier() { return fromTier; }
    public void setFromTier(String fromTier) { this.fromTier = fromTier; }

    public String getToTier() { return toTier; }
    public void setToTier(String toTier) { this.toTier = toTier; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public double getPaidPrice() { return paidPrice; }
    public void setPaidPrice(double paidPrice) { this.paidPrice = paidPrice; }

    public double getCreditApplied() { return creditApplied; }
    public void setCreditApplied(double creditApplied) { this.creditApplied = creditApplied; }

    public double getAdjustmentAmount() { return adjustmentAmount; }
    public void setAdjustmentAmount(double adjustmentAmount) { this.adjustmentAmount = adjustmentAmount; }

    public String getAdjustmentReason() { return adjustmentReason; }
    public void setAdjustmentReason(String adjustmentReason) { this.adjustmentReason = adjustmentReason; }
}
