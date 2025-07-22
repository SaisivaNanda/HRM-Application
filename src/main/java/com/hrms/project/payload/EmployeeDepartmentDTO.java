package com.hrms.project.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDepartmentDTO {

    private String departmentId;
    private String departmentName;


    private List<EmployeeTeamResponse> employeeList;
}
