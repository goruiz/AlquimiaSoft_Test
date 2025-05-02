package com.test.mi_negocio.appplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.test_alquimiasoft.mi_negocio.application.CustomerService;
import com.test_alquimiasoft.mi_negocio.domain.Interfaces.CustomerInterface;
import com.test_alquimiasoft.mi_negocio.domain.model.Address;
import com.test_alquimiasoft.mi_negocio.domain.model.Customer;
import com.test_alquimiasoft.mi_negocio.domain.model.IdentificationType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerInterface customerInterface;

    @InjectMocks
    private CustomerService customerService;

    private UUID existingId;
    private Customer existingCustomer;
    private Address mainAddress;

    @BeforeEach
    void setUp() {
        existingId = UUID.randomUUID();
        mainAddress = new Address(UUID.randomUUID(), "Gua", "Gye", "Line", true);
        existingCustomer = new Customer(
            existingId,
            IdentificationType.CEDULA,
            "0912345678",
            "Juan Pérez",
            "juan@ejemplo.com",
            "0999999999",
            mainAddress,
            Collections.emptySet()
        );
    }

    @Test
    void search_returnsList() {
        when(customerInterface.search("juan"))
            .thenReturn(List.of(existingCustomer));

        List<Customer> result = customerService.search("juan");

        assertEquals(1, result.size());
        assertEquals(existingCustomer, result.get(0));
        verify(customerInterface).search("juan");
    }

    @Test
    void create_success() {
        Customer toCreate = new Customer(
            UUID.randomUUID(),
            IdentificationType.CEDULA,
            "0987654321",
            "Ana",
            "ana@ejemplo.com",
            "0988888888",
            mainAddress,
            Collections.emptySet()
        );
        when(customerInterface.findByIdentificationNumber("0987654321"))
            .thenReturn(Optional.empty());
        when(customerInterface.save(toCreate)).thenReturn(toCreate);

        Customer saved = customerService.create(toCreate);

        assertEquals(toCreate, saved);
        verify(customerInterface).findByIdentificationNumber("0987654321");
        verify(customerInterface).save(toCreate);
    }

    @Test
    void create_duplicate_throws() {
        Customer toCreate = new Customer(
            UUID.randomUUID(),
            IdentificationType.CEDULA,
            existingCustomer.getIdentificationNumber(),
            "Ana",
            "ana@ejemplo.com",
            "0988888888",
            mainAddress,
            Collections.emptySet()
        );
        when(customerInterface.findByIdentificationNumber(existingCustomer.getIdentificationNumber()))
            .thenReturn(Optional.of(existingCustomer));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> customerService.create(toCreate));
        assertEquals("Customer with identification already exists", ex.getMessage());
        verify(customerInterface, never()).save(any());
    }

    @Test
    void update_success() {
        Customer update = new Customer(
            null,
            IdentificationType.RUC,
            "1799999999001",
            "Juan S.A.",
            "ventas@juan.com",
            "022345678",
            null,
            null
        );
        when(customerInterface.findById(existingId))
            .thenReturn(Optional.of(existingCustomer));
        when(customerInterface.findByIdentificationNumber("1799999999001"))
            .thenReturn(Optional.empty());
        Customer updated = new Customer(
            existingId,
            update.getIdentificationType(),
            update.getIdentificationNumber(),
            update.getFullName(),
            update.getEmail(),
            update.getMobileNumber(),
            mainAddress,
            Collections.emptySet()
        );
        when(customerInterface.save(existingCustomer)).thenReturn(updated);

        Customer result = customerService.update(existingId, update);

        assertEquals(existingId, result.getId());
        assertEquals("Juan S.A.", result.getFullName());
        verify(customerInterface).save(existingCustomer);
    }

    @Test
    void update_notFound_throws() {
        when(customerInterface.findById(existingId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> customerService.update(existingId, existingCustomer));
        assertEquals("Customer not found", ex.getMessage());
        verify(customerInterface, never()).save(any());
    }

    @Test
    void update_duplicateIdentification_throws() {
        Customer update = new Customer(
            null,
            IdentificationType.CEDULA,
            "DUPLICATE",
            "Juan S.A.",
            "ventas@juan.com",
            "022345678",
            null,
            null
        );
        when(customerInterface.findById(existingId))
            .thenReturn(Optional.of(existingCustomer));
        when(customerInterface.findByIdentificationNumber("DUPLICATE"))
            .thenReturn(Optional.of(existingCustomer));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> customerService.update(existingId, update));
        assertEquals("Customer with identification already exists", ex.getMessage());
        verify(customerInterface, never()).save(any());
    }

    @Test
    void delete_invokesRepository() {
        UUID id = UUID.randomUUID();
        customerService.delete(id);
        verify(customerInterface).deleteById(id);
    }
}
