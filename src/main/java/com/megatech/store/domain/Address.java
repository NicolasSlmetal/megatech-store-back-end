package com.megatech.store.domain;

import com.megatech.store.dtos.address.AddressDTO;
import com.megatech.store.exceptions.InvalidCustomerFieldException;

import java.util.regex.Pattern;

public class Address {

    public static final int ZIP_CODE_SIZE = 8;
    private Long id;
    private String street;
    private Integer number;
    private String city;
    private String state;
    private String zipcode;

    public Address(AddressDTO addressDTO) {
        setStreet(addressDTO.street());
        setNumber(addressDTO.number());
        setCity(addressDTO.city());
        setState(addressDTO.state());
        setZipCode(addressDTO.zipcode());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null) {
            throw new InvalidCustomerFieldException("Address id cannot be null");
        }
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        if (street == null || street.isEmpty()) {
            throw new InvalidCustomerFieldException("Street cannot be null or empty");
        }
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        if (number == null) {
            throw new InvalidCustomerFieldException("Number cannot be null");
        }
        this.number = number;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.isEmpty()) {
            throw new InvalidCustomerFieldException("City cannot be null or empty");
        }
        this.city = city;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state == null || state.isEmpty()) {
            throw new InvalidCustomerFieldException("State cannot be null or empty");
        }
        this.state = state;
    }
    public String getZipcode() {
        return zipcode;
    }

    public void setZipCode(String zipcode) {
        if (zipcode == null || zipcode.isEmpty()) {
            throw new InvalidCustomerFieldException("ZipCode cannot be null or empty");
        }
        zipcode = zipcode.replace("-", "");
        if (zipcode.length() != ZIP_CODE_SIZE) {
            throw new InvalidCustomerFieldException("ZipCode length must have length of 9");
        }

        if (Pattern.compile("[^0-9]").matcher(zipcode).find()) {
            throw new InvalidCustomerFieldException("ZipCode contains illegal characters");
        }
        this.zipcode = zipcode;
    }
}