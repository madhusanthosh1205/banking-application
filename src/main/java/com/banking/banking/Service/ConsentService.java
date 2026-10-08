package com.banking.banking.Service;

import com.banking.banking.Dto.ConsentRequestDTO;
import com.banking.banking.Dto.ConsentResponseDTO;
import com.banking.banking.Entity.BankAccount;
import com.banking.banking.Entity.Consent;
import com.banking.banking.Entity.Customer;
import com.banking.banking.Repository.BankAccountRepository;
import com.banking.banking.Repository.ConsentRepository;
import com.banking.banking.Repository.CustomerRepository;
import com.banking.banking.enums.ConsentStatus;
import com.banking.banking.exception.ConsentNotFoundException;

import jakarta.transaction.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;

    public ConsentService(
            ConsentRepository consentRepository,
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository
    ) {
        this.consentRepository = consentRepository;
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    // CREATE CONSENT

    @Transactional
    public ConsentResponseDTO createConsent(
            ConsentRequestDTO request,
            Authentication authentication
    ) {

        Customer customer;

        boolean isAdminOrMaker =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_admin")
                                        || authority.getAuthority().equals("ROLE_maker")
                        );

        if (isAdminOrMaker) {

            customer =
                    customerRepository.findById(
                            request.getCustomerId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Customer not found with id: "
                                            + request.getCustomerId()
                            )
                    );

        } else {

            String username = authentication.getName();

            customer =
                    customerRepository
                            .findByKeycloakUserId(username)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Customer not found"
                                    )
                            );

            if (!customer.getId()
                    .equals(request.getCustomerId())) {

                throw new RuntimeException(
                        "You cannot create consent for another customer"
                );
            }
        }

        BankAccount account =
                bankAccountRepository.findById(
                        request.getAccountId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Account not found with id: "
                                        + request.getAccountId()
                        )
                );

        // Make sure the account belongs to the customer

        if (!account.getCustomer().getId()
                .equals(customer.getId())) {

            throw new RuntimeException(
                    "Account does not belong to the selected customer"
            );
        }

        Consent consent = new Consent();

        consent.setCustomer(customer);
        consent.setAccount(account);
        consent.setPurpose(request.getPurpose());
        consent.setStatus(ConsentStatus.PENDING);
        consent.setCreatedAt(LocalDateTime.now());

        Consent savedConsent =
                consentRepository.save(consent);

        return convertToResponse(savedConsent);
    }


    // GET ALL CONSENTS

    public List<ConsentResponseDTO> getAllConsents(
            Authentication authentication) {

        boolean isCustomer =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_customer")
                        );

        if (isCustomer) {

            String username = authentication.getName();

            Customer customer =
                    customerRepository
                            .findByKeycloakUserId(username)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Customer not found"
                                    )
                            );

            return consentRepository
                    .findByCustomerId(customer.getId())
                    .stream()
                    .map(this::convertToResponse)
                    .toList();
        }

        return consentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // GET CONSENT BY ID

    public ConsentResponseDTO getConsentById(
            Long id,
            Authentication authentication) {

        Consent consent =
                consentRepository.findById(id)
                        .orElseThrow(() ->
                                new ConsentNotFoundException(
                                        "Consent not found with id: " + id
                                )
                        );

        boolean isCustomer =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_customer")
                        );

        if (isCustomer) {

            String username = authentication.getName();

            if (!consent.getCustomer()
                    .getKeycloakUserId()
                    .equals(username)) {

                throw new ConsentNotFoundException(
                        "Consent not found with id: " + id
                );
            }
        }

        return convertToResponse(consent);
    }


    // GET CONSENTS BY CUSTOMER

    public List<ConsentResponseDTO> getConsentsByCustomer(
            Long customerId,
            Authentication authentication
    ) {

        boolean isCustomer =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_customer")
                        );

        if (isCustomer) {

            String username = authentication.getName();

            Customer customer =
                    customerRepository
                            .findByKeycloakUserId(username)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Customer not found"
                                    )
                            );

            if (!customer.getId().equals(customerId)) {

                throw new ConsentNotFoundException(
                        "Consent not found"
                );
            }
        }

        return consentRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // GET CONSENTS BY ACCOUNT

    public List<ConsentResponseDTO> getConsentsByAccount(
            Long accountId,
            Authentication authentication
    ) {

        BankAccount account =
                bankAccountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Account not found with id: "
                                                + accountId
                                )
                        );

        boolean isCustomer =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_customer")
                        );

        if (isCustomer) {

            String username = authentication.getName();

            if (!account.getCustomer()
                    .getKeycloakUserId()
                    .equals(username)) {

                throw new ConsentNotFoundException(
                        "Consent not found"
                );
            }
        }

        return consentRepository
                .findByAccountId(accountId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // APPROVE CONSENT

    @Transactional
    public ConsentResponseDTO approveConsent(
            Long id,
            String reviewedBy
    ) {

        Consent consent =
                consentRepository.findById(id)
                        .orElseThrow(() ->
                                new ConsentNotFoundException(
                                        "Consent not found with id: " + id
                                )
                        );

        if (consent.getStatus() != ConsentStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending consents can be approved"
            );
        }

        consent.setStatus(ConsentStatus.APPROVED);

        consent.setReviewedAt(
                LocalDateTime.now()
        );

        consent.setReviewedBy(reviewedBy);

        Consent updatedConsent =
                consentRepository.save(consent);

        return convertToResponse(updatedConsent);
    }


    // REJECT CONSENT

    @Transactional
    public ConsentResponseDTO rejectConsent(
            Long id,
            String reviewedBy
    ) {

        Consent consent =
                consentRepository.findById(id)
                        .orElseThrow(() ->
                                new ConsentNotFoundException(
                                        "Consent not found with id: " + id
                                )
                        );

        if (consent.getStatus() != ConsentStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending consents can be rejected"
            );
        }

        consent.setStatus(ConsentStatus.REJECTED);

        consent.setReviewedAt(
                LocalDateTime.now()
        );

        consent.setReviewedBy(reviewedBy);

        Consent updatedConsent =
                consentRepository.save(consent);

        return convertToResponse(updatedConsent);
    }


    // DTO CONVERSION

    private ConsentResponseDTO convertToResponse(
            Consent consent
    ) {

        return new ConsentResponseDTO(

                consent.getId(),

                consent.getCustomer().getId(),

                consent.getCustomer().getName(),

                consent.getAccount().getId(),

                consent.getAccount().getAccountNumber(),

                consent.getPurpose(),

                consent.getStatus(),

                consent.getCreatedAt(),

                consent.getReviewedAt(),

                consent.getReviewedBy()
        );
    }
}