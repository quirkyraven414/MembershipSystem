package com.membership.dto;

import java.time.LocalDateTime;

public class PricingEstimateResponse {
    private boolean allowed;
    private String currentTier;
    private String newTier;
    private String transitionType;
    private PricingBreakdown pricing;
    private String message;
    private LocalDateTime currentExpiryDate;
    private LocalDateTime newExpiryDate;

    public static class PricingBreakdown {
        private double baseAmount;
        private double creditApplied;
        private double adjustmentAmount;
        private String adjustmentReason;
        private double finalPrice;

        public PricingBreakdown(double baseAmount, double creditApplied, double adjustmentAmount, 
                               String adjustmentReason, double finalPrice) {
            this.baseAmount = baseAmount;
            this.creditApplied = creditApplied;
            this.adjustmentAmount = adjustmentAmount;
            this.adjustmentReason = adjustmentReason;
            this.finalPrice = finalPrice;
        }

        public double getBaseAmount() { return baseAmount; }
        public void setBaseAmount(double baseAmount) { this.baseAmount = baseAmount; }

        public double getCreditApplied() { return creditApplied; }
        public void setCreditApplied(double creditApplied) { this.creditApplied = creditApplied; }

        public double getAdjustmentAmount() { return adjustmentAmount; }
        public void setAdjustmentAmount(double adjustmentAmount) { this.adjustmentAmount = adjustmentAmount; }

        public String getAdjustmentReason() { return adjustmentReason; }
        public void setAdjustmentReason(String adjustmentReason) { this.adjustmentReason = adjustmentReason; }

        public double getFinalPrice() { return finalPrice; }
        public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    }

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }

    public String getCurrentTier() { return currentTier; }
    public void setCurrentTier(String currentTier) { this.currentTier = currentTier; }

    public String getNewTier() { return newTier; }
    public void setNewTier(String newTier) { this.newTier = newTier; }

    public String getTransitionType() { return transitionType; }
    public void setTransitionType(String transitionType) { this.transitionType = transitionType; }

    public PricingBreakdown getPricing() { return pricing; }
    public void setPricing(PricingBreakdown pricing) { this.pricing = pricing; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCurrentExpiryDate() { return currentExpiryDate; }
    public void setCurrentExpiryDate(LocalDateTime currentExpiryDate) { this.currentExpiryDate = currentExpiryDate; }

    public LocalDateTime getNewExpiryDate() { return newExpiryDate; }
    public void setNewExpiryDate(LocalDateTime newExpiryDate) { this.newExpiryDate = newExpiryDate; }
}
