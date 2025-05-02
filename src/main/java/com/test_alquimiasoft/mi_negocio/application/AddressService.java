package com.test_alquimiasoft.mi_negocio.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test_alquimiasoft.mi_negocio.domain.Interfaces.AddressInterface;
import com.test_alquimiasoft.mi_negocio.domain.Interfaces.CustomerInterface;
import com.test_alquimiasoft.mi_negocio.domain.model.Address;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    // Injections
    private final AddressInterface addressInterface;
    private final CustomerInterface customerInterface;

    public AddressService(AddressInterface addressInterface,
                          CustomerInterface customerInterface) {
        this.addressInterface = addressInterface;
        this.customerInterface = customerInterface;
    }

    // Methods

    // Add a new address for an existing customer
    @Transactional
    public Address addAddress(UUID customerId, Address address) {
        customerInterface.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return addressInterface.save(customerId, address);
    }

    // List all addresses for a given customer
    @Transactional(readOnly = true)
    public List<Address> listAddresses(UUID customerId) {
        return addressInterface.findByCustomerId(customerId);
    }
}
