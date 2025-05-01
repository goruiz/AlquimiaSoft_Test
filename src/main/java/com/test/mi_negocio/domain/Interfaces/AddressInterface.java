package com.test.mi_negocio.domain.Interfaces;

import com.test.mi_negocio.domain.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressInterface {
    Optional<Address> findById(UUID id);
    List<Address> findByCustomerId(UUID customerId);
    Address save(UUID customerId, Address address);
    void deleteById(UUID id);
}
