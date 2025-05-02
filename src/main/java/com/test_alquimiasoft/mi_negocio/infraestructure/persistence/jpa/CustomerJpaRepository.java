package com.test_alquimiasoft.mi_negocio.infraestructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.test_alquimiasoft.mi_negocio.infraestructure.persistence.entity.CustomerEntity;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {

    // Methods

    // Find a customer by their identification number
    Optional<CustomerEntity> findByIdentificationNumber(String identificationNumber);

    // Search customers by full name or identification number substring
    @Query("SELECT c FROM CustomerEntity c WHERE lower(c.fullName) LIKE lower(concat('%', :query, '%')) OR c.identificationNumber LIKE concat('%', :query, '%')")
    List<CustomerEntity> search(@Param("query") String query);
}
