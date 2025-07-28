package com.hrms.project.service;
import com.hrms.project.entity.Department;
import com.hrms.project.entity.Employee;
import com.hrms.project.entity.Project;
import com.hrms.project.entity.Team;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.DepartmentNotFoundException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.*;
import com.hrms.project.repository.DepartmentRepository;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.ProjectRepository;
import com.hrms.project.repository.TeamRepository;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;





import java.io.IOException;
import java.util.*;


@Service
@CacheConfig(cacheNames = {"employee", "employeeContact", "employeeAddress","employeePrimary", "employeeJob"})
public class EmployeeServiceImpl implements EmployeeService {


    private final CacheManager cacheManager;

    @Autowired
    public EmployeeServiceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;


    @CachePut(value = "employee", key = "#employeeDTO.employeeId")
    @Override
    public EmployeeDTO createData(MultipartFile employeeImage, EmployeeDTO employeeDTO) throws IOException {

        System.out.println("Received DTO: " + employeeDTO);

        if (employeeRepository.findById(employeeDTO.getEmployeeId()).isPresent()) {
            throw new APIException("Employee already exists");
        }

        Employee employee = modelMapper.map(employeeDTO, Employee.class);

        if (employeeImage != null && !employeeImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, employeeImage);
            employee.setEmployeeImage(fileName);
        }

        if (employeeDTO.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + employeeDTO.getDepartmentId()));
            employee.setDepartment(dept);
        }

        System.out.println(employeeRepository.save(employee));

        return modelMapper.map(employee, EmployeeDTO.class);


    }

    @Cacheable(value = "employee", key = "#id")
    @Override
    public EmployeeDTO getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);

        return employeeDTO;

    }

    @Cacheable(value = "employee", key = "'all'")
    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> allEmployeeDTOs = new ArrayList<>();

        for (Employee employee : employees) {
            EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);

            allEmployeeDTOs.add(employeeDTO);
        }

        return allEmployeeDTOs;
    }

    @CachePut(value = "employee", key = "#id")
    @Override
    public EmployeeDTO updateEmployee(String id, MultipartFile employeeImage, EmployeeDTO employeeDTO) throws IOException {

        Employee updateEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        if (employeeDTO.getDepartmentId() != null) {

            updateEmployee.setDepartment(null);
            employeeRepository.save(updateEmployee);

            Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + employeeDTO.getDepartmentId()));
            updateEmployee.setDepartment(department);
        }

        String existingImage = updateEmployee.getEmployeeImage();
        modelMapper.map(employeeDTO, updateEmployee);
        if (employeeImage != null && !employeeImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, employeeImage);
            updateEmployee.setEmployeeImage(fileName);
        } else {
            updateEmployee.setEmployeeImage(existingImage);
        }



        employeeRepository.save(updateEmployee);

        return modelMapper.map(updateEmployee, EmployeeDTO.class);
    }

    @Cacheable(value = "employeeContact", key = "#employeeId")
    @Override
    public ContactDetailsDTO getEmployeeContactDetails(String employeeId) {

        Employee employeeDetails=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        ContactDetailsDTO contactDetailsDTO = modelMapper.map(employeeDetails, ContactDetailsDTO.class);
        return contactDetailsDTO;

    }

    @Cacheable(value = "employeeContact", key = "'all'")
    @Override
    public List<ContactDetailsDTO> getAllEmployeeContactDetails()
    {
        List<Employee> allEmployeeDetails=employeeRepository.findAll();
        return allEmployeeDetails.stream()
                .map(employeeDetails-> modelMapper.map(employeeDetails,ContactDetailsDTO.class))
                .toList();
    }

    @CachePut(value = "employeeContact", key = "#employeeId")
    @Override
    public ContactDetailsDTO updateContactDetails(String employeeId,ContactDetailsDTO contactDetailsDTO) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        modelMapper.map(contactDetailsDTO,employee);
        employeeRepository.save(employee);
        return modelMapper.map(employee, ContactDetailsDTO.class);
    }

    @Cacheable(value = "employeeAddress", key = "#employeeId")
    @Override
    public AddressDTO getAddress(String employeeId) {
       Employee employee=employeeRepository.findById(employeeId)
               .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
       return modelMapper.map(employee, AddressDTO.class);
    }

    @Cacheable(value = "employeeAddress", key = "'all'")
    @Override
    public List<AddressDTO> getAllAddress() {
        List<Employee> allEmployee=employeeRepository.findAll();
       return allEmployee.stream()
                .map(employeeDetails-> modelMapper.map(employeeDetails, AddressDTO.class))
                .toList();
    }

    @CachePut(value = "employeeAddress", key = "#employeeId")
    @Override
    public AddressDTO updateEmployeeAddress(String employeeId, AddressDTO addressDTO) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        modelMapper.map(addressDTO,employee);
        employeeRepository.save(employee);
        return modelMapper.map(employee, AddressDTO.class);
    }

    @Cacheable(value = "employeePrimary", key = "#employeeId")
    @Override
    public EmployeePrimaryDetailsDTO getEmployeePrimaryDetails(String employeeId) {
       Employee employeeDetails=employeeRepository.findById(employeeId)
               .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
       return modelMapper.map(employeeDetails, EmployeePrimaryDetailsDTO.class);
    }

    @CachePut(value = "employeePrimary", key = "#employeeId")
    @Override
    public EmployeePrimaryDetailsDTO updateEmployeeDetails(String employeeId, EmployeePrimaryDetailsDTO employeePrimaryDetailsDTO) {
        Employee employeeDetails=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        modelMapper.map(employeeDetails,employeePrimaryDetailsDTO);
        employeeRepository.save(employeeDetails);
        return modelMapper.map(employeeDetails, EmployeePrimaryDetailsDTO.class);

    }

    @Cacheable(value = "employeeJob", key = "#employeeId")
    @Override
    public JobDetailsDTO getJobDetails(String employeeId) {
      Employee employee=employeeRepository.findById(employeeId)
              .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
       return modelMapper.map(employee, JobDetailsDTO.class);
    }

    @CachePut(value = "employeeJob", key = "#employeeId")
    @Override
    public JobDetailsDTO updateJobDetails(String employeeId, JobDetailsDTO jobDetailsDTO) {
       Employee employee=employeeRepository.findById(employeeId)
               .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
       modelMapper.map(employee,jobDetailsDTO);
        employeeRepository.save(employee);
        return modelMapper.map(employee, JobDetailsDTO.class);
    }


    @Caching(evict = {
            @CacheEvict(value = "employee", key = "#employeeId"),
            @CacheEvict(value = "employeeContact", key = "#employeeId"),
            @CacheEvict(value = "employeeAddress", key = "#employeeId"),
            @CacheEvict(value = "employeePrimary", key = "#employeeId"),
            @CacheEvict(value = "employeeJob", key = "#employeeId") })
    public EmployeeDTO deleteEmployee(String employeeId) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));


            for (Team team : employee.getTeams()) {
                team.getEmployees().remove(employee);
            }


            for (Project project : employee.getProjects()) {
                project.getEmployees().remove(employee);
            }


            employee.getTeams().clear();
            employee.getProjects().clear();


            employeeRepository.delete(employee);

        evictEmployeeRelatedCaches(employeeId);
        removeEmployeeFromTeamAndProjectAndDepartmentCaches(employeeId);

        return modelMapper.map(employee, EmployeeDTO.class);
    }

    private void removeEmployeeFromTeamAndProjectAndDepartmentCaches(String employeeId) {
        List<Team> allTeams = teamRepository.findAll();
        List<Project> allProjects = projectRepository.findAll();
        List<Department> allDepartments = departmentRepository.findAll();


        org.springframework.cache.Cache teamCache = cacheManager.getCache("team");
        org.springframework.cache.Cache teamEmployeesCache = cacheManager.getCache("teamEmployees");
        org.springframework.cache.Cache projectCache = cacheManager.getCache("project");
        org.springframework.cache.Cache departmentCache = cacheManager.getCache("department");
        org.springframework.cache.Cache departmentEmployeesCache = cacheManager.getCache("departmentEmployees");


        for (Team team : allTeams) {
            boolean removed = team.getEmployees().removeIf(e -> e.getEmployeeId().equals(employeeId));
            if(removed){
                if (teamCache != null) {
                    teamCache.evict(team.getTeamId());
                }
                if(teamEmployeesCache != null){
                    teamEmployeesCache.evict(team.getTeamId());
                }
            }


        }


        for (Project project : allProjects) {
            boolean removed = project.getEmployees().removeIf(e -> e.getEmployeeId().equals(employeeId));
            if (removed && projectCache != null) {
                projectCache.evict(project.getProjectId());
            }
        }

        for(Department department : allDepartments) {
            boolean removed = department.getEmployee().removeIf(e -> e.getEmployeeId().equals(employeeId));
            if (removed) {
                if (departmentCache != null) {
                    departmentCache.evict(department.getDepartmentId());
                }
                if (departmentEmployeesCache != null) {
                    departmentEmployeesCache.evict(department.getDepartmentId());
                }
            }

        }

    }


    private void evictEmployeeRelatedCaches(String employeeId) {
        List<String> caches = List.of("aadhaar", "achievement", "degree", "department", "departmentEmployees", "allDepartments", "employeeDepartment", "drivingLicense", "employee", "allEmployees", "contact", "allContact", "address", "allAddress", "primary", "job", "Images", "pan", "passport", "employeeTeams", "teamEmployees", "teamProjects", "voter");

        for (String cacheName : caches) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(employeeId);
            }
        }

    }





}


















