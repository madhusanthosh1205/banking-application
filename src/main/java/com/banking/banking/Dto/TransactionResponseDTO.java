package com.banking.banking.Dto;

import com.banking.banking.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long id;
    private String transactionReference;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String description;
    private Long accountId;

    public TransactionResponseDTO(
            Long id,
            String transactionReference,
            TransactionType type,
            BigDecimal amount,
            LocalDateTime transactionDate,
            String description,
            Long accountId) {

        this.id = id;
        this.transactionReference = transactionReference;
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.accountId = accountId;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public Long getAccountId() {
        return accountId;
    }
}