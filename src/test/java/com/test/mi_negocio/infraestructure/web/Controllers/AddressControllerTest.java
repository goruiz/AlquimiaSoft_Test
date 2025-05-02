package com.test.mi_negocio.infraestructure.web.Controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.test_alquimiasoft.mi_negocio.application.AddressService;
import com.test_alquimiasoft.mi_negocio.domain.model.Address;
import com.test_alquimiasoft.mi_negocio.infraestructure.web.Controllers.AddressController;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Test
    void addAddress() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID addrId = UUID.randomUUID();
        Address saved = new Address(addrId, "Pichincha", "Quito", "Av. Amazonas 300", false);
        Mockito.when(addressService.addAddress(eq(customerId), any(Address.class))).thenReturn(saved);

        String body = """
        {
          "province":"Pichincha",
          "city":"Quito",
          "addressLine":"Av. Amazonas 300",
          "main":false
        }
        """;

        mockMvc.perform(post("/api/v1/customers/{customerId}/addresses", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(addrId.toString()))
            .andExpect(jsonPath("$.city").value("Quito"));
    }

    @Test
    void listAddresses() throws Exception {
        UUID customerId = UUID.randomUUID();
        Address a1 = new Address(UUID.randomUUID(), "Pichincha", "Quito", "Av. Amazonas", false);
        Address a2 = new Address(UUID.randomUUID(), "Guayas", "Guayaquil", "Av. 9 de Octubre", true);
        Mockito.when(addressService.listAddresses(customerId)).thenReturn(List.of(a2, a1));

        mockMvc.perform(get("/api/v1/customers/{customerId}/addresses", customerId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(a2.getId().toString()))
            .andExpect(jsonPath("$[1].province").value("Pichincha"));
    }
}
