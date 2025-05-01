package com.test.mi_negocio.infraestructure.web.DTO;

import jakarta.validation.constraints.NotBlank;

public class AddressCreateRequest {
    @NotBlank
    private String province;
    @NotBlank
    private String city;
    @NotBlank
    private String addressLine;
    private boolean main;

    public AddressCreateRequest() {
    }

    public AddressCreateRequest(String province, String city, String addressLine, boolean main) {
        this.province = province;
        this.city = city;
        this.addressLine = addressLine;
        this.main = main;
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
