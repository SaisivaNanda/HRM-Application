package com.hrms.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDepartmentDTO implements Serializable {

    private String departmentId;
    private String departmentName;


    private List<EmployeeTeamResponse> employeeList;
}
