package com.hrms.project.service;

import com.hrms.project.entity.DrivingLicense;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.DrivingLicenseDTO;
import com.hrms.project.repository.DrivingLicenseRepository;
import com.hrms.project.repository.EmployeeRepository;
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
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "drivingLicense")
public class DrivingLicenseServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DrivingLicenseRepository drivingLicenseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @CachePut(value = "drivingLicense", key = "#employeeId")
    public DrivingLicense createDrivingLicense(String employeeId, MultipartFile licenseImage,
                                               DrivingLicense drivingLicense) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (drivingLicenseRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a License assigned");
        }
        Optional<DrivingLicense> existingLicense = drivingLicenseRepository.findById(drivingLicense.getLicenseNumber());

        if (existingLicense.isPresent()) {

            DrivingLicense license = existingLicense.get();
            if (license.getEmployee() == null) {
                license.setEmployee(employee);
                return drivingLicenseRepository.save(license);
            } else {
                throw new APIException("This License is already assigned to another employee");
            }
        }

        drivingLicense.setEmployee(employee);
        if (licenseImage != null && !licenseImage.isEmpty()) {
            String image = fileService.uploadImage(path, licenseImage);
            drivingLicense.setLicenseImage(image);
        }
        return drivingLicenseRepository.save(drivingLicense);
    }

    @Cacheable(value = "drivingLicense", key = "#employeeId")
    public DrivingLicenseDTO getDrivingDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
        DrivingLicense details=employee.getDrivingLicense();

        if(details==null)
        {
            throw new APIException("This employee does not have a Driving License assigned");


        }
        return modelMapper.map(details,DrivingLicenseDTO.class);

    }

    @CachePut(value = "drivingLicense", key = "#employeeId")
    public DrivingLicense updatedrivingDetails(String employeeId,MultipartFile licenseImage, DrivingLicense drivingLicense) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        DrivingLicense existing=employee.getDrivingLicense();
        if(existing==null)
        {
            throw new APIException("This employee does not have a Driving License assigned");
        }

        if (!existing.getLicenseNumber().equals(drivingLicense.getLicenseNumber())) {
            throw new APIException("Driving  number cannot be changed once submitted");
        }
        existing.setName(drivingLicense.getName());
        existing.setDateOfBirth(drivingLicense.getDateOfBirth());
        existing.setBloodGroup(drivingLicense.getBloodGroup());
        existing.setFatherName(drivingLicense.getFatherName());
        existing.setIssueDate(drivingLicense.getIssueDate());
        existing.setExpiresOn(drivingLicense.getExpiresOn());
        existing.setAddress(drivingLicense.getAddress());

        return  drivingLicenseRepository.save(existing);


    }

    @CacheEvict(value = "drivingLicense", key = "#employeeId")
    public DrivingLicense deleteByEmployeeId(String employeeId) {
        Employee employee =employeeRepository.findById(employeeId).
                orElseThrow(()->new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        DrivingLicense drivingLicense=employee.getDrivingLicense();

        if(drivingLicense==null){
            throw new APIException("No Driving License found for employee with ID: " + employeeId);
        }
        employee.setDrivingLicense(null);
        employeeRepository.save(employee);

        drivingLicenseRepository.deleteById(drivingLicense.getLicenseNumber());
        return  drivingLicense;

    }
}