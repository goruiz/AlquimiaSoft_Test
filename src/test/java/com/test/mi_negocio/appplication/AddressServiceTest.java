package com.test.mi_negocio.appplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.test_alquimiasoft.mi_negocio.application.AddressService;
import com.test_alquimiasoft.mi_negocio.domain.Interfaces.AddressInterface;
import com.test_alquimiasoft.mi_negocio.domain.Interfaces.CustomerInterface;
import com.test_alquimiasoft.mi_negocio.domain.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressInterface addressInterface;

    @Mock
    private CustomerInterface customerInterface;

    @InjectMocks
    private AddressService addressService;

    private UUID customerId;
    private Address address;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        address = new Address(UUID.randomUUID(), "Pichincha", "Quito", "Av. Amazonas 300", false);
    }

    @Test
    void addAddress_success() {
        when(customerInterface.findById(customerId))
            .thenReturn(Optional.of(mock(com.test_alquimiasoft.mi_negocio.domain.model.Customer.class)));
        when(addressInterface.save(customerId, address)).thenReturn(address);

        Address result = addressService.addAddress(customerId, address);

        assertEquals(address, result);
        verify(addressInterface).save(customerId, address);
    }

    @Test
    void addAddress_noCustomer_throws() {
        when(customerInterface.findById(customerId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> addressService.addAddress(customerId, address));
        assertEquals("Customer not found", ex.getMessage());
        verify(addressInterface, never()).save(any(), any());
    }

    @Test
    void listAddresses_returnsList() {
        when(addressInterface.findByCustomerId(customerId))
            .thenReturn(List.of(address));

        List<Address> result = addressService.listAddresses(customerId);

        assertEquals(1, result.size());
        assertEquals(address, result.get(0));
        verify(addressInterface).findByCustomerId(customerId);
    }
}
