package com.membership.dto;

public class GetUserRequest {
    private Long userId;
    private String email;
    private Long membershipId;

    public GetUserRequest() {}

    public GetUserRequest(Long userId, String email, Long membershipId) {
        this.userId = userId;
        this.email = email;
        this.membershipId = membershipId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getMembershipId() { return membershipId; }
    public void setMembershipId(Long membershipId) { this.membershipId = membershipId; }
}
