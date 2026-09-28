package com.banking.banking.Entity;

import com.banking.banking.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "transaction_reference",
            nullable = false,
            unique = true
    )
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_type",
            nullable = false
    )
    private TransactionType transactionType;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "transaction_date",
            nullable = false
    )
    private LocalDateTime transactionDate;

    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_account_id")
    private BankAccount relatedAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_id",
            nullable = false
    )
    private BankAccount account;

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BankAccount getAccount() {
        return account;
    }

    public void setAccount(BankAccount account) {
        this.account = account;
    }
    public BankAccount getRelatedAccount() {
        return relatedAccount;
    }

    public void setRelatedAccount(BankAccount relatedAccount) {
        this.relatedAccount = relatedAccount;
    }
}