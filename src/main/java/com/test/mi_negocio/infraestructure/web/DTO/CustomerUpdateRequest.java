package com.test.mi_negocio.infraestructure.web.DTO;

import java.util.List;
import java.util.UUID;

import com.test.mi_negocio.domain.model.IdentificationType;

public class CustomerUpdateRequest {
    private UUID id;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String fullName;
    private String email;
    private String mobileNumber;
    private AddressDto mainAddress;
    private List<AddressDto> extraAddresses;

    public CustomerUpdateRequest() {
    }

    public CustomerUpdateRequest(UUID id, IdentificationType identificationType, String identificationNumber, String fullName, String email, String mobileNumber, AddressDto mainAddress, List<AddressDto> extraAddresses) {
        this.id = id;
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.mainAddress = mainAddress;
        this.extraAddresses = extraAddresses;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public IdentificationType getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public AddressDto getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(AddressDto mainAddress) {
        this.mainAddress = mainAddress;
    }

    public List<AddressDto> getExtraAddresses() {
        return extraAddresses;
    }

    public void setExtraAddresses(List<AddressDto> extraAddresses) {
        this.extraAddresses = extraAddresses;
    }
}
