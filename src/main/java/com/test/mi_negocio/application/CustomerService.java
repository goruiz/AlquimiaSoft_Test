package com.test.mi_negocio.application;

import com.test.mi_negocio.domain.Interfaces.CustomerInterface;
import com.test.mi_negocio.domain.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    // Injections
    private final CustomerInterface customerInterface;

    // Constructor
    public CustomerService(CustomerInterface customerInterface) {
        this.customerInterface = customerInterface;
    }

    // Methods

    // Retrieve customers by name or ID
    @Transactional(readOnly = true)
    public List<Customer> search(String query) {
        return customerInterface.search(query);
    }

    // Create a new customer if ID number is unique
    @Transactional
    public Customer create(Customer customer) {
        if (customerInterface.findByIdentificationNumber(customer.getIdentificationNumber()).isPresent()) {
            throw new IllegalStateException("Customer with identification already exists");
        }
        return customerInterface.save(customer);
    }

    // Update existing customer details
    @Transactional
    public Customer update(UUID id, Customer customer) {
        Customer existing = customerInterface.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        boolean duplicated =
            !existing.getIdentificationNumber().equals(customer.getIdentificationNumber()) &&
             customerInterface.findByIdentificationNumber(customer.getIdentificationNumber()).isPresent();

        if (duplicated) {
            throw new IllegalStateException("Customer with identification already exists");
        }

        existing.setIdentificationType(customer.getIdentificationType());
        existing.setIdentificationNumber(customer.getIdentificationNumber());
        existing.setFullName(customer.getFullName());
        existing.setEmail(customer.getEmail());
        existing.setMobileNumber(customer.getMobileNumber());

        return customerInterface.save(existing);
    }

    // Delete customer by ID
    @Transactional
    public void delete(UUID id) {
        customerInterface.deleteById(id);
    }
}
