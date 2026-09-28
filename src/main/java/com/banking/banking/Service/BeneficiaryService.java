package com.banking.banking.Service;

import com.banking.banking.Dto.BeneficiaryRequestDTO;
import com.banking.banking.Dto.BeneficiaryResponseDTO;
import com.banking.banking.Entity.Beneficiary;
import com.banking.banking.Entity.Customer;
import com.banking.banking.exception.BeneficiaryNotFoundException;
import com.banking.banking.exception.CustomerNotFoundException;
import com.banking.banking.Repository.BeneficiaryRepository;
import com.banking.banking.Repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerRepository customerRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository,
            CustomerRepository customerRepository) {

        this.beneficiaryRepository = beneficiaryRepository;
        this.customerRepository = customerRepository;
    }

    public BeneficiaryResponseDTO createBeneficiary(
            BeneficiaryRequestDTO request) {

        Customer customer =
                customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + request.getCustomerId()
                                )
                        );

        if (beneficiaryRepository
                .existsByCustomerIdAndAccountNumber(
                        request.getCustomerId(),
                        request.getAccountNumber())) {

            throw new IllegalArgumentException(
                    "Beneficiary with this account number already exists"
            );
        }

        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setName(request.getName());
        beneficiary.setAccountNumber(
                request.getAccountNumber()
        );
        beneficiary.setBankName(
                request.getBankName()
        );
        beneficiary.setCustomer(customer);
        beneficiary.setCreatedAt(LocalDateTime.now());

        Beneficiary savedBeneficiary =
                beneficiaryRepository.save(beneficiary);

        return convertToResponse(savedBeneficiary);
    }

    public List<BeneficiaryResponseDTO> getAllBeneficiaries() {

        return beneficiaryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public BeneficiaryResponseDTO getBeneficiaryById(Long id) {

        Beneficiary beneficiary =
                beneficiaryRepository.findById(id)
                        .orElseThrow(() ->
                                new BeneficiaryNotFoundException(
                                        "Beneficiary not found with id: "
                                                + id
                                )
                        );

        return convertToResponse(beneficiary);
    }

    public void deleteBeneficiary(Long id) {

        if (!beneficiaryRepository.existsById(id)) {

            throw new BeneficiaryNotFoundException(
                    "Beneficiary not found with id: " + id
            );
        }

        beneficiaryRepository.deleteById(id);
    }

    private BeneficiaryResponseDTO convertToResponse(
            Beneficiary beneficiary) {

        return new BeneficiaryResponseDTO(
                beneficiary.getId(),
                beneficiary.getName(),
                beneficiary.getAccountNumber(),
                beneficiary.getBankName(),
                beneficiary.getCustomer().getId(),
                beneficiary.getCreatedAt()
        );
    }
}