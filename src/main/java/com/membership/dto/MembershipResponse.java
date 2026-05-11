package com.membership.dto;

import com.membership.model.Membership;

import java.time.LocalDateTime;

public class MembershipResponse {
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

    public static MembershipResponse from(Membership membership) {
        MembershipResponse response = new MembershipResponse();
        response.membershipId = membership.getMembershipId();
        response.userId = membership.getUserId();
        response.tier = membership.getTier();
        response.duration = membership.getDuration();
        response.startDate = membership.getStartDate();
        response.expiryDate = membership.getExpiryDate();
        response.basePrice = membership.getBasePrice();
        response.paidPrice = membership.getPaidPrice();
        response.status = membership.getStatus();
        response.hasDowngraded = membership.hasDowngraded();
        return response;
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

    public boolean isHasDowngraded() { return hasDowngraded; }
    public void setHasDowngraded(boolean hasDowngraded) { this.hasDowngraded = hasDowngraded; }
}
