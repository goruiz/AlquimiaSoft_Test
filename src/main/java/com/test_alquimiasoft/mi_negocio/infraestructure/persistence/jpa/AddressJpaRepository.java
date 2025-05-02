package com.test_alquimiasoft.mi_negocio.infraestructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test_alquimiasoft.mi_negocio.infraestructure.persistence.entity.AddressEntity;

import java.util.List;
import java.util.UUID;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {

    // Methods

    // Find all addresses for a given customer
    List<AddressEntity> findByCustomerId(UUID customerId);
}
