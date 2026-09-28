package com.banking.banking.Service;

import com.banking.banking.Dto.AccountRequestDTO;
import com.banking.banking.Dto.AccountResponseDTO;
import com.banking.banking.Entity.BankAccount;
import com.banking.banking.Entity.Customer;
import com.banking.banking.exception.AccountNotFoundException;
import com.banking.banking.exception.CustomerNotFoundException;
import com.banking.banking.Repository.BankAccountRepository;
import com.banking.banking.Repository.CustomerRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;

    public BankAccountService(
            BankAccountRepository bankAccountRepository,
            CustomerRepository customerRepository) {

        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
    }

    public AccountResponseDTO createAccount(
            AccountRequestDTO request) {

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: "
                                        + request.getCustomerId()
                        )
                );

        BankAccount account = new BankAccount();

        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setCustomer(customer);

        BankAccount savedAccount =
                bankAccountRepository.save(account);

        return convertToResponse(savedAccount);
    }

    public List<AccountResponseDTO> getAllAccounts() {

        return bankAccountRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public AccountResponseDTO getAccountById(Long id) {

        BankAccount account =
                bankAccountRepository.findById(id)
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        "Account not found with id: " + id
                                )
                        );

        return convertToResponse(account);
    }

    private String generateAccountNumber() {

        return "ACC" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 10)
                        .toUpperCase();
    }

    private AccountResponseDTO convertToResponse(
            BankAccount account) {

        return new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCustomer().getId(),
                account.getCustomer().getName()
        );
    }
}