package com.test.mi_negocio.infraestructure.persistence.adapter;

import com.test.mi_negocio.domain.Interfaces.AddressInterface;
import com.test.mi_negocio.domain.model.Address;
import com.test.mi_negocio.infraestructure.persistence.entity.AddressEntity;
import com.test.mi_negocio.infraestructure.persistence.entity.CustomerEntity;
import com.test.mi_negocio.infraestructure.persistence.jpa.AddressJpaRepository;
import com.test.mi_negocio.infraestructure.persistence.jpa.CustomerJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AddressAdapter implements AddressInterface {
    private final AddressJpaRepository jpa;
    private final CustomerJpaRepository customerJpa;

    public AddressAdapter(AddressJpaRepository jpa, CustomerJpaRepository customerJpa) {
        this.jpa = jpa;
        this.customerJpa = customerJpa;
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Address> findByCustomerId(UUID customerId) {
        return jpa.findByCustomerId(customerId)
                  .stream()
                  .map(this::toDomain)
                  .collect(Collectors.toList());
    }

    @Override
    public Address save(UUID customerId, Address address) {
        AddressEntity e = new AddressEntity();
        e.setId(address.getId());
        e.setProvince(address.getProvince());
        e.setCity(address.getCity());
        e.setAddressLine(address.getAddressLine());
        e.setMain(address.isMain());
        CustomerEntity cust = customerJpa.getReferenceById(customerId);
        e.setCustomer(cust);
        AddressEntity saved = jpa.save(e);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    private Address toDomain(AddressEntity e) {
        return new Address(
            e.getId(),
            e.getProvince(),
            e.getCity(),
            e.getAddressLine(),
            e.isMain()
        );
    }
}
