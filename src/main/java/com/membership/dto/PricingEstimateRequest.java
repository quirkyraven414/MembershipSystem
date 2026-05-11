package com.membership.dto;

public class PricingEstimateRequest {
    private Long userId;
    private String newTier;

    public PricingEstimateRequest() {}

    public PricingEstimateRequest(Long userId, String newTier) {
        this.userId = userId;
        this.newTier = newTier;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNewTier() { return newTier; }
    public void setNewTier(String newTier) { this.newTier = newTier; }
}
