package com.test_alquimiasoft.mi_negocio.domain.Interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.test_alquimiasoft.mi_negocio.domain.model.Customer;

public interface CustomerInterface {

    // Methods

    // Retrieve a customer by its unique ID
    Optional<Customer> findById(UUID id);

    // Retrieve a customer by identification number
    Optional<Customer> findByIdentificationNumber(String identificationNumber);

    // Search customers by name or identification number
    List<Customer> search(String query);

    // Persist a new or updated customer
    Customer save(Customer customer);

    // Remove a customer by its ID
    void deleteById(UUID id);
}
