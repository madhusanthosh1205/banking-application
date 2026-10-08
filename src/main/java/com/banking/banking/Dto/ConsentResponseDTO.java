package com.banking.banking.Dto;

import com.banking.banking.enums.ConsentStatus;

import java.time.LocalDateTime;

public class ConsentResponseDTO {

    private Long id;

    private Long customerId;

    private String customerName;

    private Long accountId;

    private String accountNumber;

    private String purpose;

    private ConsentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    private String reviewedBy;

    public ConsentResponseDTO(
            Long id,
            Long customerId,
            String customerName,
            Long accountId,
            String accountNumber,
            String purpose,
            ConsentStatus status,
            LocalDateTime createdAt,
            LocalDateTime reviewedAt,
            String reviewedBy
    ) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.purpose = purpose;
        this.status = status;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
        this.reviewedBy = reviewedBy;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public ConsentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }
}