package com.test.mi_negocio.infraestructure.web.Controllers;

import com.test.mi_negocio.application.AddressService;
import com.test.mi_negocio.domain.model.Address;
import com.test.mi_negocio.infraestructure.web.DTO.AddressCreateRequest;
import com.test.mi_negocio.infraestructure.web.DTO.AddressDto;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/addresses")
public class AddressController {
    private final AddressService service;

    public AddressController(AddressService service) {
        this.service = service;
    }

    @PostMapping
    public AddressDto addAddress(@PathVariable UUID customerId, @Valid @RequestBody AddressCreateRequest req) {
        Address domain = new Address(UUID.randomUUID(), req.getProvince(), req.getCity(), req.getAddressLine(), req.isMain());
        Address saved = service.addAddress(customerId, domain);
        return new AddressDto(saved.getId(), saved.getProvince(), saved.getCity(), saved.getAddressLine(), saved.isMain());
    }

    @GetMapping
    public List<AddressDto> listAddresses(@PathVariable UUID customerId) {
        return service.listAddresses(customerId)
            .stream()
            .map(a -> new AddressDto(a.getId(), a.getProvince(), a.getCity(), a.getAddressLine(), a.isMain()))
            .collect(Collectors.toList());
    }
}
