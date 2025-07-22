package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.PassportDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.payload.PassportDetailsDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.PassportDetailsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PassportDetailsImpl {

    @Autowired
    private PassportDetailsRepository passportDetailsRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @CachePut(value = "passport", key = "#employeeId")
    public PassportDetailsDTO createPassport(String employeeId, MultipartFile passportImage, PassportDetails passportDetails) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (passportDetailsRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a passport assigned");
        }
        Optional<PassportDetails> existingPassport = passportDetailsRepository.findById(passportDetails.getPassportNumber());

        if (existingPassport.isPresent()) {

            PassportDetails details = existingPassport.get();
            if (details.getEmployee() == null) {
                details.setEmployee(employee);
                return modelMapper.map(passportDetailsRepository.save(details), PassportDetailsDTO.class);
            } else {
                throw new APIException("This Passport is already assigned to another employee");
            }
        }

        passportDetails.setEmployee(employee);
        if (passportImage != null && !passportImage.isEmpty()) {
            String image = fileService.uploadImage(path, passportImage);
            passportDetails.setPassportImage(image);
        }
        return modelMapper.map(passportDetailsRepository.save(passportDetails), PassportDetailsDTO.class);
    }

    @Cacheable(value = "passport", key = "#employeeId")
    public PassportDetailsDTO getPassportDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
        PassportDetails details=employee.getPassportDetails();

        if(details!=null)
        {
            PassportDetailsDTO passportDetailsDTO=modelMapper.map(details,PassportDetailsDTO.class);
            passportDetailsDTO.setEmployeeId(employeeId);
            return passportDetailsDTO;
        }
        else
        {
            throw new APIException("This employee does not have a PAN assigned");
        }



    }

    @CachePut(value = "passport", key = "#employeeId")
    public PassportDetailsDTO updatePasswordDetails(String employeeId, MultipartFile passportImage, PassportDetailsDTO passportDetailsDTO) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PassportDetails existing=passportDetailsRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new APIException("Pan Details not found for employee: " + employeeId));



        if (!existing.getPassportNumber().equals(passportDetailsDTO.getPassportNumber())) {
            throw new APIException("Passport  number cannot be changed once submitted");
        }
            existing.setCountryCode(passportDetailsDTO.getCountryCode());
            existing.setPassportType(passportDetailsDTO.getPassportType());
            existing.setDateOfBirth(passportDetailsDTO.getDateOfBirth());
            existing.setName(passportDetailsDTO.getName());
            existing.setGender(passportDetailsDTO.getGender());
            existing.setDateOfIssue(passportDetailsDTO.getDateOfIssue());
            existing.setPlaceOfIssue(passportDetailsDTO.getPlaceOfIssue());
            existing.setPlaceOfBirth(passportDetailsDTO.getPlaceOfBirth());
            existing.setDateOfExpiration(passportDetailsDTO.getDateOfExpiration());
            existing.setPassportImage(passportDetailsDTO.getPassportImage());


            PassportDetails details=passportDetailsRepository.save(existing);
            return modelMapper.map(details, PassportDetailsDTO.class);


        }



}
