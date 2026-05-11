package com.membership.service;

import com.membership.factory.MembershipFactory;
import com.membership.model.Membership;
import com.membership.model.PricingAdjustment;
import com.membership.plans.MembershipTier;
import com.membership.plans.SubscriptionDuration;
import com.membership.repository.PricingAdjustmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PricingCalculationService {
    private final PricingAdjustmentRepository adjustmentRepository;
    private final AdditionalChargeService additionalChargeService;

    public PricingCalculationService(PricingAdjustmentRepository adjustmentRepository,
                                    AdditionalChargeService additionalChargeService) {
        this.adjustmentRepository = adjustmentRepository;
        this.additionalChargeService = additionalChargeService;
    }

    public PricingResult calculateUpgradePrice(Membership currentMembership, String newTier, 
                                              SubscriptionDuration newDuration, LocalDateTime today) {
        long remainingDays = ChronoUnit.DAYS.between(today, currentMembership.getExpiryDate());
        
        if (remainingDays <= 0) {
            return calculateNewSubscription(newTier, newDuration);
        }

        MembershipTier currentTierPlan = MembershipFactory.getTier(currentMembership.getTier());
        double currentDailyRate = currentTierPlan.getBasePrice();
        double currentCredit = remainingDays * currentDailyRate / 30.0;

        MembershipTier newTierPlan = MembershipFactory.getTier(newTier);
        double newDailyRate = newTierPlan.getBasePrice();
        double newCostForRemainingDays = remainingDays * newDailyRate / 30.0;

        double baseAmount = newCostForRemainingDays - currentCredit;
        
        List<PricingAdjustment> adjustments = adjustmentRepository.findActiveByType("PERCENTAGE");
        double adjustmentAmount = 0;
        StringBuilder adjustmentReason = new StringBuilder();
        
        for (PricingAdjustment adj : adjustments) {
            if (adj.getName().equals("UPGRADE_DISCOUNT")) {
                double discount = baseAmount * Math.abs(adj.getValue()) / 100.0;
                adjustmentAmount -= discount;
                adjustmentReason.append(adj.getName()).append(" (")
                    .append(adj.getValue()).append("%), ");
            }
        }

        List<AdditionalChargeService.ChargeResult> additionalCharges = 
            additionalChargeService.evaluateCharges("UPGRADE", newTier, baseAmount);
        
        for (AdditionalChargeService.ChargeResult charge : additionalCharges) {
            adjustmentAmount += charge.getAmount();
            adjustmentReason.append(charge.getDescription()).append(", ");
        }

        double finalPrice = Math.max(0, baseAmount + adjustmentAmount);
        
        return new PricingResult(
            newCostForRemainingDays,
            currentCredit,
            adjustmentAmount,
            finalPrice,
            adjustmentReason.toString(),
            currentMembership.getExpiryDate()
        );
    }

    public PricingResult calculateDowngradePrice(String newTier, SubscriptionDuration newDuration) {
        MembershipTier newTierPlan = MembershipFactory.getTier(newTier);
        double basePrice = newDuration.calculateCost(newTierPlan.getBasePrice());

        List<PricingAdjustment> adjustments = adjustmentRepository.findActiveByType("PERCENTAGE");
        double adjustmentAmount = 0;
        StringBuilder adjustmentReason = new StringBuilder();
        
        for (PricingAdjustment adj : adjustments) {
            if (adj.getName().equals("DOWNGRADE_PENALTY")) {
                double penalty = basePrice * adj.getValue() / 100.0;
                adjustmentAmount += penalty;
                adjustmentReason.append(adj.getName()).append(" (+")
                    .append(adj.getValue()).append("%), ");
            }
        }

        List<AdditionalChargeService.ChargeResult> additionalCharges = 
            additionalChargeService.evaluateCharges("DOWNGRADE", newTier, basePrice);
        
        for (AdditionalChargeService.ChargeResult charge : additionalCharges) {
            adjustmentAmount += charge.getAmount();
            adjustmentReason.append(charge.getDescription()).append(", ");
        }

        double finalPrice = basePrice + adjustmentAmount;
        
        return new PricingResult(
            basePrice,
            0,
            adjustmentAmount,
            finalPrice,
            adjustmentReason.toString(),
            null
        );
    }

    public PricingResult calculateNewSubscription(String tier, SubscriptionDuration duration) {
        MembershipTier tierPlan = MembershipFactory.getTier(tier);
        double basePrice = duration.calculateCost(tierPlan.getBasePrice());
        
        List<AdditionalChargeService.ChargeResult> additionalCharges = 
            additionalChargeService.evaluateCharges("NEW_SUBSCRIPTION", tier, basePrice);
        
        double adjustmentAmount = 0;
        StringBuilder adjustmentReason = new StringBuilder("New subscription");
        
        for (AdditionalChargeService.ChargeResult charge : additionalCharges) {
            adjustmentAmount += charge.getAmount();
            adjustmentReason.append(", ").append(charge.getDescription());
        }
        
        double finalPrice = basePrice + adjustmentAmount;
        
        return new PricingResult(basePrice, 0, adjustmentAmount, finalPrice, adjustmentReason.toString(), null);
    }

    public static class PricingResult {
        private final double baseAmount;
        private final double creditApplied;
        private final double adjustmentAmount;
        private final double finalPrice;
        private final String adjustmentReason;
        private final LocalDateTime newExpiryDate;

        public PricingResult(double baseAmount, double creditApplied, double adjustmentAmount,
                           double finalPrice, String adjustmentReason, LocalDateTime newExpiryDate) {
            this.baseAmount = baseAmount;
            this.creditApplied = creditApplied;
            this.adjustmentAmount = adjustmentAmount;
            this.finalPrice = finalPrice;
            this.adjustmentReason = adjustmentReason;
            this.newExpiryDate = newExpiryDate;
        }

        public double getBaseAmount() { return baseAmount; }
        public double getCreditApplied() { return creditApplied; }
        public double getAdjustmentAmount() { return adjustmentAmount; }
        public double getFinalPrice() { return finalPrice; }
        public String getAdjustmentReason() { return adjustmentReason; }
        public LocalDateTime getNewExpiryDate() { return newExpiryDate; }
    }
}
