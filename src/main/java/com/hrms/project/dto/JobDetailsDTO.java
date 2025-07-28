package com.hrms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class JobDetailsDTO implements Serializable {

    private String employeeId;
    private LocalDate dateOfJoining;
    private String jobTitlePrimary;
    private String jobTitleSecondary;
    private String inProbation;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    private String probationPolicy;
    private String workerType;
    private String timeType;
    private String contractStatus;
    private LocalDate contractStartDate;



}
