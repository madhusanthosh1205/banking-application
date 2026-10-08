
package com.banking.banking.Service;

import com.banking.banking.Dto.TransactionRequestDTO;
import com.banking.banking.Dto.TransactionResponseDTO;
import com.banking.banking.Entity.BankAccount;
import com.banking.banking.Entity.Transaction;
import com.banking.banking.enums.TransactionType;
import com.banking.banking.exception.AccountNotFoundException;
import com.banking.banking.exception.InsufficientBalanceException;
import com.banking.banking.Repository.BankAccountRepository;
import com.banking.banking.Repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.banking.banking.Dto.TransferRequestDTO;
import org.springframework.security.core.Authentication;
@Service
public class TransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            BankAccountRepository bankAccountRepository,
            TransactionRepository transactionRepository) {

        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponseDTO createTransaction(
            Long accountId,
            TransactionRequestDTO request) {

        BankAccount account =
                bankAccountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        "Account not found with id: "
                                                + accountId
                                )
                        );

        BigDecimal amount = request.getAmount();

        if (request.getType() == TransactionType.DEPOSIT) {

            account.setBalance(
                    account.getBalance().add(amount)
            );

        } else if (request.getType() == TransactionType.WITHDRAWAL) {

            if (account.getBalance().compareTo(amount) < 0) {

                throw new InsufficientBalanceException(
                        "Insufficient balance for withdrawal"
                );
            }

            account.setBalance(
                    account.getBalance().subtract(amount)
            );

        } else {

            throw new IllegalArgumentException(
                    "TRANSFER will be implemented next"
            );
        }

        bankAccountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                generateTransactionReference()
        );

        transaction.setTransactionType(
                request.getType()
        );

        transaction.setAmount(amount);

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        transaction.setDescription(
                request.getDescription()
        );

        transaction.setAccount(account);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }
    @Transactional
    public TransactionResponseDTO transfer(
            Long senderAccountId,
            TransferRequestDTO request) {

        // 1. Find sender
        BankAccount sender =
                bankAccountRepository.findById(senderAccountId)
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        "Sender account not found with id: "
                                                + senderAccountId
                                )
                        );

        // 2. Find receiver
        BankAccount receiver =
                bankAccountRepository.findById(
                                request.getReceiverAccountId()
                        )
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        "Receiver account not found with id: "
                                                + request.getReceiverAccountId()
                                )
                        );

        // 3. Sender and receiver cannot be same
        if (sender.getId().equals(receiver.getId())) {

            throw new IllegalArgumentException(
                    "Sender and receiver accounts cannot be the same"
            );
        }

        // 4. Check balance
        BigDecimal amount = request.getAmount();

        if (sender.getBalance().compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance for transfer"
            );
        }

        // 5. Debit sender
        sender.setBalance(
                sender.getBalance().subtract(amount)
        );

        // 6. Credit receiver
        receiver.setBalance(
                receiver.getBalance().add(amount)
        );

        // 7. Save both accounts
        bankAccountRepository.save(sender);
        bankAccountRepository.save(receiver);

        // 8. Create transaction record
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                generateTransactionReference()
        );

        transaction.setTransactionType(
                TransactionType.TRANSFER
        );

        transaction.setAmount(amount);

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        transaction.setDescription(
                request.getDescription()
        );

        transaction.setAccount(sender);

        transaction.setRelatedAccount(receiver);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return convertToResponse(savedTransaction);
    }

    public List<TransactionResponseDTO> getTransactionsByAccount(
            Long accountId,
            Authentication authentication) {

        BankAccount account =
                bankAccountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        "Account not found with id: " + accountId
                                )
                        );

        String username = authentication.getName();

        boolean adminOrMaker =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_admin")
                                        || authority.getAuthority().equals("ROLE_maker")
                        );

        if (!adminOrMaker &&
                !account.getCustomer()
                        .getKeycloakUserId()
                        .equals(username)) {

            throw new AccountNotFoundException(
                    "Account not found with id: " + accountId
            );
        }

        return transactionRepository
                .findByAccountId(accountId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    private String generateTransactionReference() {

        return "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }

    private TransactionResponseDTO convertToResponse(
            Transaction transaction) {

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getAccount().getId()
        );
    }
}