package com.test_alquimiasoft.mi_negocio.domain.Interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.test_alquimiasoft.mi_negocio.domain.model.Address;

public interface AddressInterface {

    // Methods

    // Find an address by its unique ID
    Optional<Address> findById(UUID id);

    // List all addresses belonging to a specific customer
    List<Address> findByCustomerId(UUID customerId);

    // Save a new or updated address for the given customer
    Address save(UUID customerId, Address address);

    // Delete an address by its ID
    void deleteById(UUID id);
}
