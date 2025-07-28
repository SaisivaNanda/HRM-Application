package com.hrms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTeamResponse implements Serializable {


    private String employeeId;
    private String displayName;
    private String jobTitlePrimary;
    private String workEmail;
    private String workNumber;

}
