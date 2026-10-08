package com.banking.banking.Repository;

import com.banking.banking.Entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository
        extends JpaRepository<BankAccount, Long> {

    boolean existsByAccountNumber(String accountNumber);

    List<BankAccount> findByCustomerKeycloakUserId(String keycloakUserId);
}