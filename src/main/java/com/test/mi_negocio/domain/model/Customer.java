package com.test.mi_negocio.domain.model;

import java.util.UUID;
import java.util.Set;

public class Customer {

    // Variables
    private UUID id;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String fullName;
    private String email;
    private String mobileNumber;
    private Address mainAddress;
    private Set<Address> extraAddresses;

    // Constructor
    public Customer(UUID id,
                    IdentificationType identificationType,
                    String identificationNumber,
                    String fullName,
                    String email,
                    String mobileNumber,
                    Address mainAddress,
                    Set<Address> extraAddresses) {
        this.id = id;
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.mainAddress = mainAddress;
        this.extraAddresses = extraAddresses;
    }

    // Methods

    // Getters and setters
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

    public Address getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(Address mainAddress) {
        this.mainAddress = mainAddress;
    }

    public Set<Address> getExtraAddresses() {
        return extraAddresses;
    }

    public void setExtraAddresses(Set<Address> extraAddresses) {
        this.extraAddresses = extraAddresses;
    }
}
