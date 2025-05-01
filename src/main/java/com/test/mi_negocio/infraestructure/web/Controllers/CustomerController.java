package com.test.mi_negocio.infraestructure.web.Controllers;

import com.test.mi_negocio.application.CustomerService;
import com.test.mi_negocio.domain.model.Address;
import com.test.mi_negocio.domain.model.Customer;
import com.test.mi_negocio.infraestructure.web.DTO.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomerDto> search(@RequestParam(required = false) String query) {
        return service.search(query)
            .stream()
            .map(c -> {
                Address main = c.getMainAddress();
                AddressDto mainDto = null;
                if (main != null) {
                    mainDto = new AddressDto(main.getId(), main.getProvince(), main.getCity(), main.getAddressLine(), main.isMain());
                }
                List<AddressDto> extra = Optional.ofNullable(c.getExtraAddresses()).orElse(Collections.emptySet())
                    .stream()
                    .map(a -> new AddressDto(a.getId(), a.getProvince(), a.getCity(), a.getAddressLine(), a.isMain()))
                    .collect(Collectors.toList());
                return new CustomerDto(c.getId(), c.getIdentificationType(), c.getIdentificationNumber(), c.getFullName(), c.getEmail(), c.getMobileNumber(), mainDto, extra);
            })
            .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<CustomerDto> create(@Valid @RequestBody CustomerCreateRequest req) {
        Address mat = new Address(UUID.randomUUID(), req.getAddress().getProvince(), req.getAddress().getCity(), req.getAddress().getAddressLine(), req.getAddress().isMain());
        Customer domain = new Customer(UUID.randomUUID(), req.getIdentificationType(), req.getIdentificationNumber(), req.getFullName(), req.getEmail(), req.getMobileNumber(), mat, Collections.emptySet());
        Customer saved = service.create(domain);
        Address main = saved.getMainAddress();
        AddressDto mainDto = null;
        if (main != null) {
            mainDto = new AddressDto(main.getId(), main.getProvince(), main.getCity(), main.getAddressLine(), main.isMain());
        }
        List<AddressDto> extra = Optional.ofNullable(saved.getExtraAddresses()).orElse(Collections.emptySet())
            .stream()
            .map(a -> new AddressDto(a.getId(), a.getProvince(), a.getCity(), a.getAddressLine(), a.isMain()))
            .collect(Collectors.toList());
        CustomerDto dto = new CustomerDto(saved.getId(), saved.getIdentificationType(), saved.getIdentificationNumber(), saved.getFullName(), saved.getEmail(), saved.getMobileNumber(), mainDto, extra);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public CustomerDto update(@PathVariable UUID id, @Valid @RequestBody CustomerUpdateRequest req) {
        Customer domain = new Customer(id, req.getIdentificationType(), req.getIdentificationNumber(), req.getFullName(), req.getEmail(), req.getMobileNumber(), null, null);
        Customer updated = service.update(id, domain);
        Address main = updated.getMainAddress();
        AddressDto mainDto = null;
        if (main != null) {
            mainDto = new AddressDto(main.getId(), main.getProvince(), main.getCity(), main.getAddressLine(), main.isMain());
        }
        List<AddressDto> extra = Optional.ofNullable(updated.getExtraAddresses()).orElse(Collections.emptySet())
            .stream()
            .map(a -> new AddressDto(a.getId(), a.getProvince(), a.getCity(), a.getAddressLine(), a.isMain()))
            .collect(Collectors.toList());
        return new CustomerDto(updated.getId(), updated.getIdentificationType(), updated.getIdentificationNumber(), updated.getFullName(), updated.getEmail(), updated.getMobileNumber(), mainDto, extra);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
