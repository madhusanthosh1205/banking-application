package com.banking.banking.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransferRequestDTO {

    @NotNull(message = "Receiver account ID is required")
    private Long receiverAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Transfer amount must be greater than zero")
    private BigDecimal amount;

    private String description;

    public TransferRequestDTO() {
    }

    public Long getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(Long receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}