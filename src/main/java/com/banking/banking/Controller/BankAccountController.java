package com.banking.banking.Controller;

import com.banking.banking.Dto.AccountRequestDTO;
import com.banking.banking.Dto.AccountResponseDTO;
import com.banking.banking.Service.BankAccountService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(
            BankAccountService bankAccountService) {

        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountRequestDTO request) {

        AccountResponseDTO response =
                bankAccountService.createAccount(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>>
    getAllAccounts() {

        return ResponseEntity.ok(
                bankAccountService.getAllAccounts()
        );
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> getAccountById(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                bankAccountService.getAccountById(accountId)
        );
    }
}