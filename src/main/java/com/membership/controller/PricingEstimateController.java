package com.membership.controller;

import com.membership.dto.ErrorResponse;
import com.membership.dto.PricingEstimateRequest;
import com.membership.dto.PricingEstimateResponse;
import com.membership.model.Membership;
import com.membership.plans.SubscriptionDuration;
import com.membership.repository.MembershipRepository;
import com.membership.service.MembershipTransitionService;
import com.membership.service.PricingCalculationService;
import com.membership.transition.TransitionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/pricing")
public class PricingEstimateController {
    private final MembershipRepository membershipRepository;
    private final MembershipTransitionService transitionService;
    private final PricingCalculationService pricingService;

    public PricingEstimateController(MembershipRepository membershipRepository,
                                    MembershipTransitionService transitionService,
                                    PricingCalculationService pricingService) {
        this.membershipRepository = membershipRepository;
        this.transitionService = transitionService;
        this.pricingService = pricingService;
    }

    @PostMapping("/estimate")
    public ResponseEntity<?> estimatePricing(@RequestBody PricingEstimateRequest request) {
        try {
            Optional<Membership> currentOpt = membershipRepository.findActiveByUserId(request.getUserId());
            
            if (currentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("No Active Membership", 
                        "User does not have an active membership to transition from"));
            }

            Membership current = currentOpt.get();
            String currentTier = current.getTier();
            String newTier = request.getNewTier();

            PricingEstimateResponse response = new PricingEstimateResponse();
            response.setCurrentTier(currentTier);
            response.setNewTier(newTier);
            response.setCurrentExpiryDate(current.getExpiryDate());

            if (currentTier.equals(newTier)) {
                response.setAllowed(false);
                response.setMessage("Cannot transition to the same tier");
                return ResponseEntity.ok(response);
            }

            int currentTierLevel = getTierLevel(currentTier);
            int newTierLevel = getTierLevel(newTier);
            boolean isUpgrade = newTierLevel > currentTierLevel;
            
            response.setTransitionType(isUpgrade ? "UPGRADE" : "DOWNGRADE");

            TransitionResult validation;
            if (isUpgrade) {
                validation = transitionService.requestUpgrade(currentTier, newTier);
            } else {
                validation = transitionService.requestDowngrade(currentTier, newTier, false);
            }

            response.setAllowed(validation.isAllowed());
            response.setMessage(validation.getMessage());

            if (!validation.isAllowed()) {
                return ResponseEntity.ok(response);
            }

            SubscriptionDuration duration = SubscriptionDuration.valueOf(current.getDuration());
            PricingCalculationService.PricingResult pricing;

            if (isUpgrade) {
                pricing = pricingService.calculateUpgradePrice(current, newTier, duration, LocalDateTime.now());
            } else {
                pricing = pricingService.calculateDowngradePrice(newTier, duration);
            }

            response.setPricing(new PricingEstimateResponse.PricingBreakdown(
                pricing.getBaseAmount(),
                pricing.getCreditApplied(),
                pricing.getAdjustmentAmount(),
                pricing.getAdjustmentReason(),
                pricing.getFinalPrice()
            ));
            
            response.setNewExpiryDate(pricing.getNewExpiryDate());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid Request", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Error", e.getMessage()));
        }
    }

    private int getTierLevel(String tier) {
        return switch (tier) {
            case "BRONZE" -> 1;
            case "SILVER" -> 2;
            case "GOLD" -> 3;
            case "PLATINUM" -> 4;
            default -> throw new IllegalArgumentException("Invalid tier: " + tier);
        };
    }
}
