package com.membership.dto;

public class TransitionRequest {
    private Long membershipId;
    private Long userId;
    private String newTier;
    private boolean isSubscriptionEnd;

    public Long getMembershipId() { return membershipId; }
    public void setMembershipId(Long membershipId) { this.membershipId = membershipId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNewTier() { return newTier; }
    public void setNewTier(String newTier) { this.newTier = newTier; }

    public boolean isSubscriptionEnd() { return isSubscriptionEnd; }
    public void setSubscriptionEnd(boolean subscriptionEnd) { isSubscriptionEnd = subscriptionEnd; }
}
