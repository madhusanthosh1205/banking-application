package com.banking.banking.Controller;

import com.banking.banking.Dto.TransactionRequestDTO;
import com.banking.banking.Dto.TransactionResponseDTO;
import com.banking.banking.Dto.TransferRequestDTO;
import com.banking.banking.Service.TransactionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequestDTO request) {

        TransactionResponseDTO response =
                transactionService.createTransaction(
                        accountId,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @PathVariable Long accountId,
            @Valid @RequestBody TransferRequestDTO request) {

        TransactionResponseDTO response =
                transactionService.transfer(
                        accountId,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @PathVariable Long accountId,
            Authentication authentication) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccount(
                        accountId,
                        authentication
                )
        );
    }
}