
package com.banking.banking.Repository;

import com.banking.banking.Entity.Consent;
import com.banking.banking.enums.ConsentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentRepository
        extends JpaRepository<Consent, Long> {

    List<Consent> findByCustomerId(Long customerId);

    List<Consent> findByAccountId(Long accountId);

    List<Consent> findByStatus(ConsentStatus status);
}