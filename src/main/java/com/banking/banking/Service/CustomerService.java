package com.banking.banking.Service;

import com.banking.banking.Dto.CustomerRequestDTO;
import com.banking.banking.Dto.CustomerResponseDTO;
import com.banking.banking.Entity.Customer;
import com.banking.banking.Repository.CustomerRepository;
import org.springframework.stereotype.Service;
import com.banking.banking.exception.CustomerNotFoundException;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDTO createCustomer(
            CustomerRequestDTO request) {

        Customer customer = new Customer();

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer savedCustomer =
                customerRepository.save(customer);

        return convertToResponse(savedCustomer);
    }

    public List<CustomerResponseDTO> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public CustomerResponseDTO getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found with id: " + id)
                );

        return convertToResponse(customer);
    }

    public CustomerResponseDTO updateCustomer(
            Long id,
            CustomerRequestDTO request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found with id: " + id)
                );

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer updatedCustomer =
                customerRepository.save(customer);

        return convertToResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {

        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(
                    "Customer not found with id: " + id
            );
        }

        customerRepository.deleteById(id);
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