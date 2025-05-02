package com.test.mi_negocio.infraestructure.persistence.jpa;

import com.test.mi_negocio.infraestructure.persistence.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {

    // Methods

    // Find all addresses for a given customer
    List<AddressEntity> findByCustomerId(UUID customerId);
}
