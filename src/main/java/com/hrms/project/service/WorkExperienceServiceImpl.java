package com.hrms.project.service;


import com.hrms.project.entity.Employee;
import com.hrms.project.entity.WorkExperienceDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.WorkExperienceDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.WorkExperienceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@CacheConfig(cacheNames = {"workExperience"})
public class WorkExperienceServiceImpl {

    @Value("${project.image}")
    private String path;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileService fileService;
    @Autowired
    private WorkExperienceRepository workExperienceRepository;
    @Autowired
    private ModelMapper modelMapper;

    @CachePut(value = "workExperience", key = "#employeeId")
    public WorkExperienceDetails createExperenceByEmployeId(String employeeId, MultipartFile uploadImage, WorkExperienceDetails workExperienceDetails) throws IOException {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with id " + employeeId));

        if(uploadImage != null && !uploadImage.isEmpty()){
            String fileName = fileService.uploadImage(path,uploadImage);
            workExperienceDetails.setUploadFiles(fileName);

        }
        workExperienceDetails.setEmployee(employee);
        return workExperienceRepository.save(workExperienceDetails);
    }

    @CachePut(value = "workExperience", key = "#employeeId")
    public WorkExperienceDetails updateExperience(String employeeId,
                                                  MultipartFile uploadImage,
                                                  WorkExperienceDetails updatedData,
                                                  Long id) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        WorkExperienceDetails existing = workExperienceRepository.findById(id)
                .orElseThrow(() -> new APIException("Work experience not found"));

        if (!existing.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This work experience does not belong to employee with ID: " + employeeId);
        }

        String oldFile = existing.getUploadFiles();

        modelMapper.map(updatedData, existing);
        existing.setId(id);
        existing.setEmployee(employee);

        if (uploadImage != null && !uploadImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, uploadImage);
            existing.setUploadFiles(fileName);
        } else {
            existing.setUploadFiles(oldFile);
        }

        return workExperienceRepository.save(existing);
    }

    @Cacheable(value = "workExperience", key = "#employeeId")
    public List<WorkExperienceDTO> getExperience(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        List<WorkExperienceDetails>experienceDetails=employee.getWorkExperienceDetails();
        return experienceDetails.stream()
                .map(exp->modelMapper.map(exp,WorkExperienceDTO.class))
                .toList();
    }

    @CacheEvict(value = "workExperience", key = "#employeeId")
    public WorkExperienceDetails deleteExperienceById(String employeeId, Long id) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        WorkExperienceDetails workExperienceDetails=workExperienceRepository.findById(id)
                .orElseThrow(()-> new APIException("Experience not found with ID: " + id));

        if (!workExperienceDetails.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This experience does not belong to the given employee.");
        }

        workExperienceRepository.delete(workExperienceDetails);
        return  workExperienceDetails;
    }
}