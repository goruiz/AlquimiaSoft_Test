package com.test.mi_negocio.domain.model;

import java.util.UUID;

public class Address {

    // Variables
    private UUID id;
    private String province;
    private String city;
    private String addressLine;
    private boolean main;

    // Constructor
    public Address(UUID id, String province, String city, String addressLine, boolean main) {
        this.id = id;
        this.province = province;
        this.city = city;
        this.addressLine = addressLine;
        this.main = main;
    }

    // Methods

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }
}
