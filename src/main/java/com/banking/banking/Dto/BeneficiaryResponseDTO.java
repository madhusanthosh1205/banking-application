package com.banking.banking.Dto;

import java.time.LocalDateTime;

public class BeneficiaryResponseDTO {

    private Long id;
    private String name;
    private String accountNumber;
    private String bankName;
    private Long customerId;
    private LocalDateTime createdAt;

    public BeneficiaryResponseDTO(
            Long id,
            String name,
            String accountNumber,
            String bankName,
            Long customerId,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.customerId = customerId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}