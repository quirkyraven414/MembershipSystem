package com.membership.dto;

public class CreateMembershipRequest {
    private Long userId;
    private String tier;
    private String duration;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
