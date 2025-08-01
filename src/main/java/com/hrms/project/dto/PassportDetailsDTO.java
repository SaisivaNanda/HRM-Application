package com.hrms.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PassportDetailsDTO implements Serializable{

    private String passportNumber;
    private String countryCode;
    private String passportType;
    private LocalDate dateOfBirth;
    private String name;
    private String gender;
    private LocalDate dateOfIssue;
    private String placeOfIssue;
    private String placeOfBirth;
    private LocalDate dateOfExpiration;
    private String address;

}
