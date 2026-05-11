package com.membership.controller;

import com.membership.dto.CreateMembershipRequest;
import com.membership.dto.ErrorResponse;
import com.membership.dto.MembershipResponse;
import com.membership.dto.TransitionRequest;
import com.membership.model.Membership;
import com.membership.model.MembershipHistory;
import com.membership.model.TransactionLog;
import com.membership.plans.SubscriptionDuration;
import com.membership.service.UserMembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {
    private final UserMembershipService membershipService;

    public MembershipController(UserMembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<MembershipResponse> getCurrentMembership(@PathVariable Long userId) {
        Membership membership = membershipService.getCurrentMembership(userId);
        if (membership == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(MembershipResponse.from(membership));
    }

    @PostMapping
    public ResponseEntity<?> createMembership(@RequestBody CreateMembershipRequest request) {
        try {
            Membership membership = membershipService.createMembership(
                request.getUserId(),
                request.getTier(),
                SubscriptionDuration.valueOf(request.getDuration())
            );
            return ResponseEntity.ok(MembershipResponse.from(membership));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("User Not Found", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid State", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Error", e.getMessage()));
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradeMembership(@RequestBody TransitionRequest request) {
        try {
            Membership membership = membershipService.upgradeMembership(
                request.getUserId(),
                request.getNewTier()
            );
            return ResponseEntity.ok(MembershipResponse.from(membership));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Upgrade Failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Error", e.getMessage()));
        }
    }

    @PostMapping("/downgrade")
    public ResponseEntity<?> downgradeMembership(@RequestBody TransitionRequest request) {
        try {
            Membership membership = membershipService.downgradeMembership(
                request.getUserId(),
                request.getNewTier(),
                request.isSubscriptionEnd()
            );
            return ResponseEntity.ok(MembershipResponse.from(membership));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Downgrade Failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Error", e.getMessage()));
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelMembership(@RequestBody TransitionRequest request) {
        try {
            membershipService.cancelMembership(request.getUserId(), request.isSubscriptionEnd());
            return ResponseEntity.ok().body(new ErrorResponse("Success", "Membership cancelled successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Cancellation Failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Error", e.getMessage()));
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<MembershipHistory>> getMembershipHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getMembershipHistory(userId));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<TransactionLog>> getTransactionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getTransactionHistory(userId));
    }
}
