package com.hrms.project.service;


import com.hrms.project.entity.Employee;
import com.hrms.project.entity.Project;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.PublicEmployeeDetails;
import com.hrms.project.dto.PublicProjectDTO;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"employees", "employeeDetails","employeeHeader"})
public class PublicEmployeeService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Cacheable("employees")
    public List<PublicEmployeeDetails> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(emp ->
                {
                    PublicEmployeeDetails publicEmployeeDetails = new PublicEmployeeDetails();

                    publicEmployeeDetails.setName(emp.getDisplayName());
                    publicEmployeeDetails.setWorkEmail(emp.getWorkEmail());
                    publicEmployeeDetails.setLocation(emp.getLocation());
                    publicEmployeeDetails.setJobTitlePrimary(emp.getJobTitlePrimary());
                    publicEmployeeDetails.setDepartment(emp.getDepartment().getDepartmentName());
                    publicEmployeeDetails.setEmployeeImage(emp.getEmployeeImage());

                    return publicEmployeeDetails;
                })
                .toList();
    }

    @Cacheable(value = "employeeDetails", key = "#employeeId")
    public PublicEmployeeDetails getEmployeeDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id: " + employeeId + " not found"));

        PublicEmployeeDetails publicEmployeeDetails = new PublicEmployeeDetails();

        publicEmployeeDetails.setEmployeeId(employee.getEmployeeId());
        publicEmployeeDetails.setName(employee.getDisplayName());
        publicEmployeeDetails.setWorkEmail(employee.getWorkEmail());
        publicEmployeeDetails.setLocation(employee.getLocation());

        publicEmployeeDetails.setEmployeeImage(employee.getEmployeeImage());
        publicEmployeeDetails.setJobTitlePrimary(employee.getJobTitlePrimary());
        publicEmployeeDetails.setDepartment(employee.getDepartment().getDepartmentName());
        publicEmployeeDetails.setContact(employee.getWorkNumber());

        publicEmployeeDetails.setSkills(employee.getSkills());

        List<Project> projects=employee.getProjects();

        publicEmployeeDetails.setProjects( projects.stream().map(
                project ->
                {

                    PublicProjectDTO projectDTO = new  PublicProjectDTO();
                    projectDTO.setProjectName(project.getTitle());
                    projectDTO.setProjectDescription(project.getDescription());
                    return projectDTO;
                }).toList()
        );

        return publicEmployeeDetails;
        }

    @Cacheable(value = "employeeHeader", key = "#employeeId")
    public PublicEmployeeDetails getHeaderDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id: " + employeeId + " not found"));

        PublicEmployeeDetails publicEmployeeDetails = new PublicEmployeeDetails();

        publicEmployeeDetails.setEmployeeId(employee.getEmployeeId());
        publicEmployeeDetails.setName(employee.getDisplayName());
        publicEmployeeDetails.setWorkEmail(employee.getWorkEmail());
        publicEmployeeDetails.setLocation(employee.getLocation());

        publicEmployeeDetails.setEmployeeImage(employee.getEmployeeImage());
        publicEmployeeDetails.setJobTitlePrimary(employee.getJobTitlePrimary());
        publicEmployeeDetails.setDepartment(employee.getDepartment().getDepartmentName());
        publicEmployeeDetails.setContact(employee.getWorkNumber());

        return publicEmployeeDetails;

    }
}
