package com.banking.banking.Controller;

import com.banking.banking.Dto.BeneficiaryRequestDTO;
import com.banking.banking.Dto.BeneficiaryResponseDTO;
import com.banking.banking.Service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody BeneficiaryRequestDTO request) {

        BeneficiaryResponseDTO response =
                beneficiaryService.createBeneficiary(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponseDTO>>
    getAllBeneficiaries() {

        return ResponseEntity.ok(
                beneficiaryService.getAllBeneficiaries()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponseDTO>
    getBeneficiaryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                beneficiaryService.getBeneficiaryById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(
            @PathVariable Long id) {

        beneficiaryService.deleteBeneficiary(id);

        return ResponseEntity.noContent().build();
    }
}