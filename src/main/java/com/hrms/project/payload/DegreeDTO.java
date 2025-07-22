package com.hrms.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DegreeDTO {


    private String degree;
    private String branchOrSpecialization;
    private LocalDate yearOfJoining;
    private LocalDate yearOfCompletion;
    private String cgpaOrPercentage;
    private String universityOrCollege;
    private String addFiles;


}