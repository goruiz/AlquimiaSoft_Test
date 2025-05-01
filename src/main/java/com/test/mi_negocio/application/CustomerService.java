package com.test.mi_negocio.application;

import com.test.mi_negocio.domain.Interfaces.CustomerInterface;
import com.test.mi_negocio.domain.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerInterface customerInterface;

    public CustomerService(CustomerInterface customerInterface) {
        this.customerInterface = customerInterface;
    }

    @Transactional(readOnly = true)
    public List<Customer> search(String query) {
        return customerInterface.search(query);
    }

    @Transactional
    public Customer create(Customer customer) {
        if (customerInterface.findByIdentificationNumber(customer.getIdentificationNumber()).isPresent()) {
            throw new IllegalStateException("Customer with identification already exists");
        }
        return customerInterface.save(customer);
    }

    @Transactional
    public Customer update(UUID id, Customer customer) {
        Customer existing = customerInterface.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (!existing.getIdentificationNumber().equals(customer.getIdentificationNumber())
            && customerInterface.findByIdentificationNumber(customer.getIdentificationNumber()).isPresent()) {
            throw new IllegalStateException("Customer with identification already exists");
        }

        // Solo modificamos los campos de cliente, sin tocar direcciones
        existing.setIdentificationType(customer.getIdentificationType());
        existing.setIdentificationNumber(customer.getIdentificationNumber());
        existing.setFullName(customer.getFullName());
        existing.setEmail(customer.getEmail());
        existing.setMobileNumber(customer.getMobileNumber());

        return customerInterface.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        customerInterface.deleteById(id);
    }
}
