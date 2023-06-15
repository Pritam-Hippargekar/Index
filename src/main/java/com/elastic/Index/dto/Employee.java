package com.elastic.Index.dto;

import lombok.Data;

import java.util.List;

@Data
public class Employee {
    private Long employeeId;
    private String employeeName;
    private String birthDate;
    private String mobileNumber;
    private String emailAddress;
    private String aadhaarId;
    private String panId;
    private Boolean status;//(active/inactive)
    private Integer priority;//(for fetching record based on priority)
    private String[] skills;
    private Department department;
    private List<Address> addresses;
}
