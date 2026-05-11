package com.membership.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_logs")
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    
    private Long userId;
    private Long membershipId;
    private double amount;
    private String type;
    private double creditUsed;
    private double netAmount;
    private LocalDateTime paymentDate;
    private String status;

    public TransactionLog() {}

    public TransactionLog(Long userId, Long membershipId, double amount, String type,
                         double creditUsed, double netAmount, String status) {
        this.userId = userId;
        this.membershipId = membershipId;
        this.amount = amount;
        this.type = type;
        this.creditUsed = creditUsed;
        this.netAmount = netAmount;
        this.paymentDate = LocalDateTime.now();
        this.status = status;
    }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMembershipId() { return membershipId; }
    public void setMembershipId(Long membershipId) { this.membershipId = membershipId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getCreditUsed() { return creditUsed; }
    public void setCreditUsed(double creditUsed) { this.creditUsed = creditUsed; }

    public double getNetAmount() { return netAmount; }
    public void setNetAmount(double netAmount) { this.netAmount = netAmount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
