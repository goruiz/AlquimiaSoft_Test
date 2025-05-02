package com.test.mi_negocio.infraestructure.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.test.mi_negocio.domain.model.IdentificationType;

public class CustomerCreateRequest {

    // Variables
    @NotNull
    private IdentificationType identificationType;
    @NotBlank
    private String identificationNumber;
    @NotBlank
    private String fullName;
    @Email
    private String email;
    private String mobileNumber;
    @NotNull
    private AddressCreateRequest address;

    // Constructors
    public CustomerCreateRequest() {
    }

    public CustomerCreateRequest(IdentificationType identificationType,
                                 String identificationNumber,
                                 String fullName,
                                 String email,
                                 String mobileNumber,
                                 AddressCreateRequest address) {
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
    }

    // Methods

    // Getters and setters
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

    public AddressCreateRequest getAddress() {
        return address;
    }

    public void setAddress(AddressCreateRequest address) {
        this.address = address;
    }
}
