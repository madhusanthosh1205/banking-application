package com.banking.banking.Controller;

import com.banking.banking.Dto.ConsentRequestDTO;
import com.banking.banking.Dto.ConsentResponseDTO;
import com.banking.banking.Service.ConsentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(
            ConsentService consentService
    ) {
        this.consentService = consentService;
    }


    // CREATE CONSENT

    @PostMapping
    public ResponseEntity<ConsentResponseDTO> createConsent(
            @Valid @RequestBody ConsentRequestDTO request,
            Authentication authentication
    ) {

        ConsentResponseDTO response =
                consentService.createConsent(
                        request,
                        authentication
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // GET ALL CONSENTS

    @GetMapping
    public ResponseEntity<List<ConsentResponseDTO>>
    getAllConsents(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                consentService.getAllConsents(
                        authentication
                )
        );
    }


    // GET CONSENT BY ID

    @GetMapping("/{id}")
    public ResponseEntity<ConsentResponseDTO>
    getConsentById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                consentService.getConsentById(
                        id,
                        authentication
                )
        );
    }


    // GET CONSENTS BY CUSTOMER

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ConsentResponseDTO>>
    getConsentsByCustomer(
            @PathVariable Long customerId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                consentService.getConsentsByCustomer(
                        customerId,
                        authentication
                )
        );
    }


    // GET CONSENTS BY ACCOUNT

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ConsentResponseDTO>>
    getConsentsByAccount(
            @PathVariable Long accountId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                consentService.getConsentsByAccount(
                        accountId,
                        authentication
                )
        );
    }


    // APPROVE CONSENT

    @PutMapping("/{id}/approve")
    public ResponseEntity<ConsentResponseDTO>
    approveConsent(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String reviewedBy =
                authentication.getName();

        return ResponseEntity.ok(
                consentService.approveConsent(
                        id,
                        reviewedBy
                )
        );
    }


    // REJECT CONSENT

    @PutMapping("/{id}/reject")
    public ResponseEntity<ConsentResponseDTO>
    rejectConsent(
            @PathVariable Long id,
            Authentication authentication
    ) {

        String reviewedBy =
                authentication.getName();

        return ResponseEntity.ok(
                consentService.rejectConsent(
                        id,
                        reviewedBy
                )
        );
    }

}