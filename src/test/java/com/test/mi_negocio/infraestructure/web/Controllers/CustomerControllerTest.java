package com.test.mi_negocio.infraestructure.web.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test_alquimiasoft.mi_negocio.application.CustomerService;
import com.test_alquimiasoft.mi_negocio.domain.model.Address;
import com.test_alquimiasoft.mi_negocio.domain.model.Customer;
import com.test_alquimiasoft.mi_negocio.domain.model.IdentificationType;
import com.test_alquimiasoft.mi_negocio.infraestructure.web.Controllers.CustomerController;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchReturnsClients() throws Exception {
        UUID id = UUID.randomUUID();
        UUID addrId = UUID.randomUUID();
        Address main = new Address(addrId, "Pichincha", "Quito", "Av. Amazonas 300", true);
        Customer customer = new Customer(
            id,
            IdentificationType.CEDULA,
            "0912345678",
            "Juan Pérez",
            "juan@ejemplo.com",
            "0999999999",
            main,
            Collections.emptySet()
        );
        Mockito.when(customerService.search("juan")).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/v1/customers")
                .param("query", "juan")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(id.toString()))
            .andExpect(jsonPath("$[0].identificationNumber").value("0912345678"))
            .andExpect(jsonPath("$[0].mainAddress.id").value(addrId.toString()));
    }

    @Test
    void createClient() throws Exception {
        UUID id = UUID.randomUUID();
        UUID addrId = UUID.randomUUID();
        Address main = new Address(addrId, "Guayas", "Guayaquil", "Av. 9 de Octubre 100", true);
        Customer input = new Customer(
            id,
            IdentificationType.CEDULA,
            "0912345678",
            "Juan Pérez",
            "juan@ejemplo.com",
            "0999999999",
            main,
            Collections.emptySet()
        );
        Mockito.when(customerService.create(any(Customer.class))).thenReturn(input);

        String body = """
        {
          "identificationType":"CEDULA",
          "identificationNumber":"0912345678",
          "fullName":"Juan Pérez",
          "email":"juan@ejemplo.com",
          "mobileNumber":"0999999999",
          "address":{
            "province":"Guayas",
            "city":"Guayaquil",
            "addressLine":"Av. 9 de Octubre 100",
            "main":true
          }
        }
        """;

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.mainAddress.id").value(addrId.toString()));
    }

    @Test
    void updateClient() throws Exception {
        UUID id = UUID.randomUUID();
        UUID addrId = UUID.randomUUID();
        Address main = new Address(addrId, "Guayas", "Guayaquil", "Av. 9 de Octubre 100", true);
        Customer updated = new Customer(
            id,
            IdentificationType.RUC,
            "1799999999001",
            "Juan Pérez S.A.",
            "ventas@juan.com",
            "022345678",
            main,
            Collections.emptySet()
        );
        Mockito.when(customerService.update(eq(id), any(Customer.class))).thenReturn(updated);

        String body = """
        {
          "identificationType":"RUC",
          "identificationNumber":"1799999999001",
          "fullName":"Juan Pérez S.A.",
          "email":"ventas@juan.com",
          "mobileNumber":"022345678"
        }
        """;

        mockMvc.perform(put("/api/v1/customers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.identificationType").value("RUC"));
    }

    @Test
    void deleteClient() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/v1/customers/{id}", id))
            .andExpect(status().isNoContent());
        Mockito.verify(customerService).delete(id);
    }
}
