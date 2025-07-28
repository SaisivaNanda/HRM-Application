package com.hrms.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePrimaryDetailsDTO implements Serializable {

   // private String employeeId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String displayName;
    private String maritalStatus;
    private String bloodGroup;
    private String physicallyHandicapped;
    private String nationality;
    private String gender;
    private LocalDate dateOfBirth;
    private String employeeImage;
 private LocalDateTime shiftStartTime;
 private LocalDateTime shiftEndTime;

}
