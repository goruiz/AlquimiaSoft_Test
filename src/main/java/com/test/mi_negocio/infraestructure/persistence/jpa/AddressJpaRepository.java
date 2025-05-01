package com.test.mi_negocio.infraestructure.persistence.jpa;

import com.test.mi_negocio.infraestructure.persistence.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {
    List<AddressEntity> findByCustomerId(UUID customerId);
}
