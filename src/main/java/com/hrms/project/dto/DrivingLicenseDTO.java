package com.hrms.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrivingLicenseDTO implements Serializable {


    private String licenseNumber;
    private String name;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String fatherName;
    private LocalDate issueDate;
    private LocalDate expiresOn;
    private String address;

}
