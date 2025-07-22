package com.hrms.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarDTO {

    private String aadhaarNumber;
    private String enrollmentNumber;
    private LocalDate dateOfBirth;
    private String aadhaarName;
    private String address;
    private String gender;

}
