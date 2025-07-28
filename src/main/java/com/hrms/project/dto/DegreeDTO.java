package com.hrms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DegreeDTO implements Serializable {


    private String degree;
    private String branchOrSpecialization;
    private LocalDate startMonth;
    private LocalDate endMonth;
    private LocalDate startYear;
    private LocalDate endYear;
    private String cgpaOrPercentage;
    private String universityOrCollege;
    private String addFiles;


}