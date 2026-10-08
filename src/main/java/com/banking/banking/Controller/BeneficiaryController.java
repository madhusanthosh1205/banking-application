package com.banking.banking.Controller;

import com.banking.banking.Dto.BeneficiaryRequestDTO;
import com.banking.banking.Dto.BeneficiaryResponseDTO;
import com.banking.banking.Service.BeneficiaryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService) {

        this.beneficiaryService = beneficiaryService;
    }

    @PostMapping
    public ResponseEntity<BeneficiaryResponseDTO>
    createBeneficiary(
            @Valid @RequestBody BeneficiaryRequestDTO request,
            Authentication authentication) {

        BeneficiaryResponseDTO response =
                beneficiaryService.createBeneficiary(
                        request,
                        authentication
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponseDTO>>
    getAllBeneficiaries(
            Authentication authentication) {

        return ResponseEntity.ok(
                beneficiaryService.getAllBeneficiaries(
                        authentication
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponseDTO>
    getBeneficiaryById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                beneficiaryService.getBeneficiaryById(
                        id,
                        authentication
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(
            @PathVariable Long id) {

        beneficiaryService.deleteBeneficiary(id);

        return ResponseEntity.noContent().build();
    }
}