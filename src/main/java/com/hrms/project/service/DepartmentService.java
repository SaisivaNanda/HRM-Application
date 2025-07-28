package com.hrms.project.service;

import com.hrms.project.dto.DepartmentDTO;
import com.hrms.project.dto.EmployeeDepartmentDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DepartmentService {


    DepartmentDTO saveDepartment(DepartmentDTO departmentDTO);

    EmployeeDepartmentDTO getEmployeesByDepartmentId(String departmentId);

    DepartmentDTO updateDepartment(String departmentId, DepartmentDTO departmentDTO);

    List<DepartmentDTO> getAllDepartmentDetails();

    DepartmentDTO getByDepartmentId(String departmentId);

  EmployeeDepartmentDTO getEmployeeByEmployeeId(String employeeId);

   String deleteDepartment(String departmentId);
}
