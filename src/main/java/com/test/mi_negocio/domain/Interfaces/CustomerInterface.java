package com.test.mi_negocio.domain.Interfaces;

import com.test.mi_negocio.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CustomerInterface {


    Optional<Customer> findById(UUID id);

    Optional<Customer> findByIdentificationNumber(String identificationNumber);

    List<Customer> search(String query);

    Customer save(Customer customer);

    void deleteById(UUID id);
}
