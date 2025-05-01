package com.test.mi_negocio.infraestructure.persistence.entity;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "address")
public class AddressEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "province", nullable = false)
    private String province;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "address_line", nullable = false)
    private String addressLine;
    @Column(name = "is_main", nullable = false)
    private boolean main;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    public AddressEntity() {
    }

    public AddressEntity(UUID id, String province, String city, String addressLine, boolean main, CustomerEntity customer) {
        this.id = id;
        this.province = province;
        this.city = city;
        this.addressLine = addressLine;
        this.main = main;
        this.customer = customer;
    }

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

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }
}
