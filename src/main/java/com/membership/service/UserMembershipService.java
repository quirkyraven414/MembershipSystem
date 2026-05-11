package com.membership.service;

import com.membership.model.*;
import com.membership.plans.SubscriptionDuration;
import com.membership.repository.*;
import com.membership.transition.TransitionResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserMembershipService {
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipHistoryRepository historyRepository;
    private final TransactionLogRepository transactionRepository;
    private final MembershipTransitionService transitionService;
    private final PricingCalculationService pricingService;

    public UserMembershipService(UserRepository userRepository,
                                MembershipRepository membershipRepository,
                                MembershipHistoryRepository historyRepository,
                                TransactionLogRepository transactionRepository,
                                MembershipTransitionService transitionService,
                                PricingCalculationService pricingService) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.historyRepository = historyRepository;
        this.transactionRepository = transactionRepository;
        this.transitionService = transitionService;
        this.pricingService = pricingService;
    }

    public Membership getCurrentMembership(Long userId) {
        return membershipRepository.findActiveByUserId(userId).orElse(null);
    }

    public Membership createMembership(Long userId, String tier, SubscriptionDuration duration) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        Optional<Membership> existing = membershipRepository.findActiveByUserId(userId);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already has an active membership");
        }

        PricingCalculationService.PricingResult pricing = 
            pricingService.calculateNewSubscription(tier, duration);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime expiryDate = calculateExpiryDate(startDate, duration);

        Membership membership = new Membership(
            userId, tier, duration.name(), startDate, expiryDate,
            pricing.getBaseAmount(), pricing.getFinalPrice()
        );
        membership = membershipRepository.save(membership);

        historyRepository.save(new MembershipHistory(
            userId, membership.getMembershipId(), tier, duration.name(),
            startDate, expiryDate, "CREATED", null, tier,
            pricing.getBaseAmount(), pricing.getFinalPrice(), 0, 0, "New subscription"
        ));

        transactionRepository.save(new TransactionLog(
            userId, membership.getMembershipId(), pricing.getFinalPrice(),
            "PURCHASE", 0, pricing.getFinalPrice(), "COMPLETED"
        ));

        return membership;
    }

    public Membership upgradeMembership(Long userId, String newTier) {
        Membership current = getCurrentMembership(userId);
        if (current == null) {
            throw new IllegalStateException("No active membership found");
        }

        TransitionResult validation = transitionService.requestUpgrade(current.getTier(), newTier);
        if (!validation.isAllowed()) {
            throw new IllegalStateException("Upgrade not allowed: " + validation.getMessage());
        }

        SubscriptionDuration duration = SubscriptionDuration.valueOf(current.getDuration());
        PricingCalculationService.PricingResult pricing = 
            pricingService.calculateUpgradePrice(current, newTier, duration, LocalDateTime.now());

        current.setStatus("CANCELLED");
        membershipRepository.save(current);

        historyRepository.save(new MembershipHistory(
            userId, current.getMembershipId(), current.getTier(), current.getDuration(),
            current.getStartDate(), current.getExpiryDate(), "CANCELLED",
            current.getTier(), null, current.getBasePrice(), current.getPaidPrice(),
            pricing.getCreditApplied(), 0, "Cancelled for upgrade"
        ));

        Membership newMembership = new Membership(
            userId, newTier, duration.name(), LocalDateTime.now(), pricing.getNewExpiryDate(),
            pricing.getBaseAmount(), pricing.getFinalPrice()
        );
        newMembership.setHasDowngraded(false);
        newMembership = membershipRepository.save(newMembership);

        historyRepository.save(new MembershipHistory(
            userId, newMembership.getMembershipId(), newTier, duration.name(),
            LocalDateTime.now(), pricing.getNewExpiryDate(), "UPGRADED",
            current.getTier(), newTier, pricing.getBaseAmount(), pricing.getFinalPrice(),
            pricing.getCreditApplied(), pricing.getAdjustmentAmount(), pricing.getAdjustmentReason()
        ));

        transactionRepository.save(new TransactionLog(
            userId, newMembership.getMembershipId(), pricing.getBaseAmount(),
            "UPGRADE", pricing.getCreditApplied(), pricing.getFinalPrice(), "COMPLETED"
        ));

        return newMembership;
    }

    public Membership downgradeMembership(Long userId, String newTier, boolean isSubscriptionEnd) {
        Membership current = getCurrentMembership(userId);
        if (current == null) {
            throw new IllegalStateException("No active membership found");
        }

        if (current.hasDowngraded()) {
            throw new IllegalStateException("Downgrade denied: Already downgraded once in this cycle");
        }

        TransitionResult validation = transitionService.requestDowngrade(
            current.getTier(), newTier, isSubscriptionEnd
        );
        if (!validation.isAllowed()) {
            throw new IllegalStateException("Downgrade not allowed: " + validation.getMessage());
        }

        SubscriptionDuration duration = SubscriptionDuration.valueOf(current.getDuration());
        PricingCalculationService.PricingResult pricing = 
            pricingService.calculateDowngradePrice(newTier, duration);

        current.setStatus("CANCELLED");
        membershipRepository.save(current);

        historyRepository.save(new MembershipHistory(
            userId, current.getMembershipId(), current.getTier(), current.getDuration(),
            current.getStartDate(), current.getExpiryDate(), "CANCELLED",
            current.getTier(), null, current.getBasePrice(), current.getPaidPrice(),
            0, 0, "Cancelled for downgrade"
        ));

        LocalDateTime newStartDate = isSubscriptionEnd ? current.getExpiryDate() : LocalDateTime.now();
        LocalDateTime newExpiryDate = calculateExpiryDate(newStartDate, duration);

        Membership newMembership = new Membership(
            userId, newTier, duration.name(), newStartDate, newExpiryDate,
            pricing.getBaseAmount(), pricing.getFinalPrice()
        );
        newMembership.setHasDowngraded(true);
        newMembership.setDowngradeDate(LocalDateTime.now());
        newMembership = membershipRepository.save(newMembership);

        historyRepository.save(new MembershipHistory(
            userId, newMembership.getMembershipId(), newTier, duration.name(),
            newStartDate, newExpiryDate, "DOWNGRADED",
            current.getTier(), newTier, pricing.getBaseAmount(), pricing.getFinalPrice(),
            0, pricing.getAdjustmentAmount(), pricing.getAdjustmentReason()
        ));

        transactionRepository.save(new TransactionLog(
            userId, newMembership.getMembershipId(), pricing.getBaseAmount(),
            "DOWNGRADE", 0, pricing.getFinalPrice(), "COMPLETED"
        ));

        return newMembership;
    }

    public void cancelMembership(Long userId, boolean isSubscriptionEnd) {
        Membership current = getCurrentMembership(userId);
        if (current == null) {
            throw new IllegalStateException("No active membership found");
        }

        TransitionResult validation = transitionService.requestCancellation(
            current.getTier(), isSubscriptionEnd
        );
        if (!validation.isAllowed()) {
            throw new IllegalStateException("Cancellation not allowed: " + validation.getMessage());
        }

        current.setStatus("CANCELLED");
        membershipRepository.save(current);

        historyRepository.save(new MembershipHistory(
            userId, current.getMembershipId(), current.getTier(), current.getDuration(),
            current.getStartDate(), current.getExpiryDate(), "CANCELLED",
            current.getTier(), null, current.getBasePrice(), current.getPaidPrice(),
            0, 0, "User cancelled membership"
        ));
    }

    public List<MembershipHistory> getMembershipHistory(Long userId) {
        return historyRepository.findByUserId(userId);
    }

    public List<TransactionLog> getTransactionHistory(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    private LocalDateTime calculateExpiryDate(LocalDateTime startDate, SubscriptionDuration duration) {
        return switch (duration) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case HALF_YEARLY -> startDate.plusMonths(6);
            case ANNUAL -> startDate.plusYears(1);
        };
    }
}
