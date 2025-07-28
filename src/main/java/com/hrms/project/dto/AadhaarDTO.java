package com.hrms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarDTO implements Serializable {

    private String aadhaarNumber;
    private String enrollmentNumber;
    private LocalDate dateOfBirth;
    private String aadhaarName;
    private String address;
    private String gender;

}
