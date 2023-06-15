package com.elastic.Index.dto;

import lombok.Data;

@Data
public class Address {
    private Long addressId;
    private String addressLine1;
    private String AddressLine2;
    private String country;
    private String state;
    private String district;
    private String city;
    private Integer pinCode;
}
