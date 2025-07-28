package com.hrms.project.service;

import com.hrms.project.entity.Department;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.DepartmentNotFoundException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.DepartmentDTO;
import com.hrms.project.dto.EmployeeDepartmentDTO;
import com.hrms.project.dto.EmployeeTeamResponse;
import com.hrms.project.repository.DepartmentRepository;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = {"department", "departmentEmployees", "allDepartments","employeeDepartment"})
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ModelMapper modelMapper;

    @CachePut(value = "department", key = "#departmentDTO.departmentId")
    @CacheEvict(value = "allDepartments", key = "'all'")
    @Override
    public DepartmentDTO saveDepartment(DepartmentDTO departmentDTO) {

        Department dept=new Department();
        dept.setDepartmentId(departmentDTO.getDepartmentId());
        dept.setDepartmentName(departmentDTO.getDepartmentName());
        dept.setDepartmentDescription(departmentDTO.getDepartmentDescription());
        departmentRepository.save(dept);
        return modelMapper.map(departmentDTO,DepartmentDTO.class);

    }


    @Cacheable(value = "departmentEmployees", key = "#departmentId")
    @Override
    public EmployeeDepartmentDTO getEmployeesByDepartmentId(String departmentId) {
       Department dept=departmentRepository.findById(departmentId)
               .orElseThrow(()->new DepartmentNotFoundException(departmentId));
       EmployeeDepartmentDTO employeeDepartmentDTO=new EmployeeDepartmentDTO();
       employeeDepartmentDTO.setDepartmentId(dept.getDepartmentId());
       employeeDepartmentDTO.setDepartmentName(dept.getDepartmentName());


           List<EmployeeTeamResponse> employeeTeamResponses= dept.getEmployee().stream()
               .map(employee -> {
                   Employee empl = employeeRepository.findById(employee.getEmployeeId())
                           .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with employee id :"+employee.getEmployeeId()));
                   EmployeeTeamResponse employeeTeamResponse = new EmployeeTeamResponse();

                   employeeTeamResponse.setEmployeeId(empl.getEmployeeId());
                   employeeTeamResponse.setDisplayName(empl.getDisplayName());
                   employeeTeamResponse.setJobTitlePrimary(empl.getJobTitlePrimary());
                   employeeTeamResponse.setWorkEmail(empl.getWorkEmail());
                   employeeTeamResponse.setWorkNumber(empl.getWorkNumber());
                   return employeeTeamResponse;

               }).toList();

           employeeDepartmentDTO.setEmployeeList(employeeTeamResponses);

           return employeeDepartmentDTO;

    }

    @CachePut(value = "department", key = "#departmentId")
    @CacheEvict(value = "allDepartments", key = "'all'")
    @Override
    public DepartmentDTO updateDepartment(String departmentId, DepartmentDTO departmentDTO) {

        Department dept=departmentRepository.findById(departmentId)
                        .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: "+ departmentId));

        dept.setDepartmentName(departmentDTO.getDepartmentName());
        dept.setDepartmentDescription(departmentDTO.getDepartmentDescription());


        return modelMapper.map( departmentRepository.save(dept), DepartmentDTO.class);

    }

    @Cacheable(value = "allDepartments", key = "'all'")
    @Override
    public List<DepartmentDTO> getAllDepartmentDetails() {
        List<Department> dept=departmentRepository.findAll();
        List<DepartmentDTO> departmentDTOS=new ArrayList<>();
        for(Department deptDTO:dept){
            DepartmentDTO departmentDTO=modelMapper.map(deptDTO,DepartmentDTO.class);
            departmentDTOS.add(departmentDTO);

        }
        return departmentDTOS;
    }

    @Cacheable(value = "department", key = "#departmentId")
    @Override
    public DepartmentDTO getByDepartmentId(String departmentId) {

        Department dept=departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: "+ departmentId));

        return modelMapper.map(dept ,DepartmentDTO.class);


    }

    @Cacheable(value = "employeeDepartment", key = "#employeeId")
    @Override
    public EmployeeDepartmentDTO getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        Department dept = employee.getDepartment();

        EmployeeDepartmentDTO employeeDepartmentDTO = new EmployeeDepartmentDTO();
        employeeDepartmentDTO.setDepartmentId(dept.getDepartmentId());
        employeeDepartmentDTO.setDepartmentName(dept.getDepartmentName());

        List<EmployeeTeamResponse> employeeTeamResponses = dept.getEmployee().stream()
                .map(emp -> {
                    Employee empl = employeeRepository.findById(emp.getEmployeeId())
                            .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + emp.getEmployeeId()));

                    EmployeeTeamResponse response = new EmployeeTeamResponse();
                    response.setEmployeeId(empl.getEmployeeId());
                    response.setDisplayName(empl.getDisplayName());
                    response.setJobTitlePrimary(empl.getJobTitlePrimary());
                    response.setWorkEmail(empl.getWorkEmail());
                    response.setWorkNumber(empl.getWorkNumber());

                    return response;
                }).toList();

        employeeDepartmentDTO.setEmployeeList(employeeTeamResponses);

        return employeeDepartmentDTO;
    }

    @Caching(evict={
            @CacheEvict(value="department", key="#departmentId"),
            @CacheEvict(value = "allDepartments", key = "'all'")
    })
    @Override
    public String deleteDepartment(String departmentId) {

        Department dept=departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + departmentId));
        List<Employee> employees = dept.getEmployee();
        for (Employee emp : employees) {
            emp.setDepartment(null);
            employeeRepository.save(emp);
        }

        departmentRepository.delete(dept);
        return "Department deleted successfully";

    }

}
