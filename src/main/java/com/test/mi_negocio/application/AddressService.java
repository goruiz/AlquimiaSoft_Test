package com.test.mi_negocio.application;

import com.test.mi_negocio.domain.Interfaces.AddressInterface;
import com.test.mi_negocio.domain.Interfaces.CustomerInterface;
import com.test.mi_negocio.domain.model.Address;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {
    private final AddressInterface addressInterface;
    private final CustomerInterface customerInterface;

    public AddressService(AddressInterface addressInterface, CustomerInterface customerInterface) {
        this.addressInterface = addressInterface;
        this.customerInterface = customerInterface;
    }

    @Transactional
    public Address addAddress(UUID customerId, Address address) {
        customerInterface.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return addressInterface.save(customerId, address);
    }

    @Transactional(readOnly = true)
    public List<Address> listAddresses(UUID customerId) {
        return addressInterface.findByCustomerId(customerId);
    }
}
