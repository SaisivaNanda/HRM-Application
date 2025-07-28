package com.hrms.project.service;

import com.hrms.project.entity.AadhaarCardDetails;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.AadhaarDTO;
import com.hrms.project.repository.AadhaarDetailsRepository;
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

@Service
@CacheConfig(cacheNames = {"aadhaar"})
public class AadhaarServiceImpl {

    @Autowired
    private AadhaarDetailsRepository aadhaarDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @CachePut(value = "aadhaar", key = "#aadhaarCardDetails.aadhaarNumber")
    public AadhaarCardDetails createAadhaar(String employeeId, MultipartFile aadhaarImage, AadhaarCardDetails aadhaarCardDetails) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("employee not found with id: " + employeeId));

        if (aadhaarDetailsRepository.findByEmployee_EmployeeId(employeeId) != null) {
            throw new APIException("This employee already has Aadhaar assigned");
        }

        AadhaarCardDetails cardDetails;

        if (aadhaarDetailsRepository.findById(aadhaarCardDetails.getAadhaarNumber()).isEmpty()) {

            if (aadhaarImage != null && !aadhaarImage.isEmpty()) {
                String fileName = fileService.uploadImage(path, aadhaarImage);
                aadhaarCardDetails.setUploadAadhaar(fileName);
            }

            aadhaarCardDetails.setEmployee(employee);
            cardDetails = aadhaarDetailsRepository.save(aadhaarCardDetails);

        } else {
            AadhaarCardDetails details = aadhaarDetailsRepository.findById(aadhaarCardDetails.getAadhaarNumber()).get();

            if (details.getEmployee() == null) {
                details.setEmployee(employee);
                cardDetails = aadhaarDetailsRepository.save(details);
            } else {
                throw new APIException("Current Aadhaar card is already assigned to another employee");
            }
        }

        return cardDetails;
    }


    @Cacheable(value = "aadhaar", key = "#employeeId")
    public AadhaarDTO getAadhaarByEmployeeId(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId).orElseThrow(() ->
                new EmployeeNotFoundException("Employee Not Found with id: " + employeeId));

        AadhaarCardDetails aadhaarCardDetails=employee.getAadhaarCardDetails();
        if(aadhaarCardDetails==null)
        {
            throw new APIException("Aadhaar card Details not found for the employee with id: " + employeeId);
        }

        return modelMapper.map(aadhaarCardDetails,AadhaarDTO.class);

    }


    @CachePut(value = "aadhaar", key = "#employeeId")
    public AadhaarCardDetails updateAadhaar(String employeeId,MultipartFile aadhaarImage, AadhaarCardDetails aadhaarCardDetails) throws IOException {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with the id: " + employeeId));

        AadhaarCardDetails existing=employee.getAadhaarCardDetails();
        if(existing==null)
        {
            throw new APIException("Aadhaar card Details not found for employee with id: " + employeeId);
        }

        if (!existing.getAadhaarNumber().equals(aadhaarCardDetails.getAadhaarNumber())) {
            throw new APIException("Aadhaar number cannot be changed once submitted");
        }
        if (aadhaarImage != null && !aadhaarImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, aadhaarImage);
            existing.setUploadAadhaar(fileName);
        }

        existing.setDateOfBirth(aadhaarCardDetails.getDateOfBirth());
        existing.setAadhaarName(aadhaarCardDetails.getAadhaarName());
        existing.setGender(aadhaarCardDetails.getGender());
        existing.setAddress(aadhaarCardDetails.getAddress());

        return aadhaarDetailsRepository.save(existing);

    }

    @CacheEvict(value="aadhaar", key="employeeId")
    public AadhaarCardDetails deleteAadharByEmployeeId(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()->new  EmployeeNotFoundException("Employee Not Found with the id: " + employeeId));


        AadhaarCardDetails aadhaarDetails = employee.getAadhaarCardDetails();

        if (aadhaarDetails == null) {
            throw new APIException("No Aadhaar details found for employee with id: " + employeeId);
        }
        employee.setAadhaarCardDetails(null);
        employeeRepository.save(employee);
        aadhaarDetailsRepository.deleteById(aadhaarDetails.getAadhaarNumber());
        return aadhaarDetails;
    }
}