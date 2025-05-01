package com.test.mi_negocio.infraestructure.persistence.adapter;

import com.test.mi_negocio.domain.Interfaces.CustomerInterface;
import com.test.mi_negocio.domain.model.Customer;
import com.test.mi_negocio.domain.model.Address;
import com.test.mi_negocio.infraestructure.persistence.entity.CustomerEntity;
import com.test.mi_negocio.infraestructure.persistence.entity.AddressEntity;
import com.test.mi_negocio.infraestructure.persistence.jpa.CustomerJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CustomerAdapter implements CustomerInterface {
    private final CustomerJpaRepository jpa;

    public CustomerAdapter(CustomerJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByIdentificationNumber(String identificationNumber) {
        return jpa.findByIdentificationNumber(identificationNumber).map(this::toDomain);
    }

    @Override
    public List<Customer> search(String query) {
        return jpa.search(query).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        CustomerEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    private Customer toDomain(CustomerEntity e) {
        List<AddressEntity> ents = e.getAddresses() == null ? Collections.emptyList() : e.getAddresses();
        AddressEntity mainEnt = ents.stream().filter(AddressEntity::isMain).findFirst().orElse(null);
        Address main = mainEnt == null ? null : new Address(mainEnt.getId(), mainEnt.getProvince(), mainEnt.getCity(), mainEnt.getAddressLine(), true);
        Set<Address> extras = ents.stream().filter(a -> !a.isMain()).map(a -> new Address(a.getId(), a.getProvince(), a.getCity(), a.getAddressLine(), false)).collect(Collectors.toSet());
        return new Customer(e.getId(), e.getIdentificationType(), e.getIdentificationNumber(), e.getFullName(), e.getEmail(), e.getMobileNumber(), main, extras);
    }

    private CustomerEntity toEntity(Customer c) {
        CustomerEntity e = new CustomerEntity();
        e.setId(c.getId());
        e.setIdentificationType(c.getIdentificationType());
        e.setIdentificationNumber(c.getIdentificationNumber());
        e.setFullName(c.getFullName());
        e.setEmail(c.getEmail());
        e.setMobileNumber(c.getMobileNumber());
        List<AddressEntity> list = new ArrayList<>();
        if (c.getMainAddress() != null) {
            Address ma = c.getMainAddress();
            list.add(new AddressEntity(ma.getId(), ma.getProvince(), ma.getCity(), ma.getAddressLine(), true, e));
        }
        if (c.getExtraAddresses() != null) {
            for (Address a : c.getExtraAddresses()) {
                list.add(new AddressEntity(a.getId(), a.getProvince(), a.getCity(), a.getAddressLine(), false, e));
            }
        }
        e.setAddresses(list);
        return e;
    }
}
