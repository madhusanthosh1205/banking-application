package com.banking.banking.Service;

import com.banking.banking.Dto.CustomerRequestDTO;
import com.banking.banking.Dto.CustomerResponseDTO;
import com.banking.banking.Entity.Customer;
import com.banking.banking.Repository.CustomerRepository;
import com.banking.banking.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KeycloakAdminService keycloakAdminService;

    public CustomerService(
            CustomerRepository customerRepository,
            KeycloakAdminService keycloakAdminService) {

        this.customerRepository = customerRepository;
        this.keycloakAdminService = keycloakAdminService;
    }
    public CustomerResponseDTO createCustomer(
            CustomerRequestDTO request) {

        // 1. Create user in Keycloak
        String keycloakUserId =
                keycloakAdminService.createCustomerUser(
                        request.getName(),
                        request.getEmail(),
                        request.getPassword()
                );

        try {

            // 2. Create customer in PostgreSQL
            Customer customer = new Customer();

            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());
            customer.setKeycloakUserId(keycloakUserId);

            Customer savedCustomer =
                    customerRepository.save(customer);

            // 3. Return response
            return convertToResponse(savedCustomer);

        } catch (Exception e) {

            // 4. PostgreSQL failed → remove Keycloak user
            try {
                keycloakAdminService.deleteUser(keycloakUserId);
            } catch (Exception cleanupException) {
                // Log cleanup failure
                System.err.println(
                        "Failed to remove Keycloak user: "
                                + keycloakUserId
                );
            }

            throw new RuntimeException(
                    "Customer creation failed. Keycloak user was rolled back.",
                    e
            );
        }
    }

    public List<CustomerResponseDTO> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public CustomerResponseDTO getCustomerById(Long id) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: " + id
                                )
                        );

        return convertToResponse(customer);
    }

    public CustomerResponseDTO updateCustomer(
            Long id,
            CustomerRequestDTO request) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: " + id
                                )
                        );

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        /*
         * Password is intentionally NOT updated here.
         *
         * Customer password belongs to Keycloak.
         */

        Customer updatedCustomer =
                customerRepository.save(customer);

        return convertToResponse(updatedCustomer);
    }

    @Transactional

    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + id
                        )
                );

        String keycloakUserId = customer.getKeycloakUserId();

        if (keycloakUserId != null && !keycloakUserId.isBlank()) {

            keycloakAdminService.deleteUser(keycloakUserId);
        }

        customerRepository.delete(customer);
    }

    private CustomerResponseDTO convertToResponse(
            Customer customer) {

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}