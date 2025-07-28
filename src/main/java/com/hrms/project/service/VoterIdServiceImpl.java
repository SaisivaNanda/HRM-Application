package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.VoterDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.VoterDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.VoterIdRepository;
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
@CacheConfig(cacheNames = {"voter"})
public class VoterIdServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private VoterIdRepository voterIdRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @CachePut(value = "voter", key = "#employeeId")
    public VoterDetails createVoter(String employeeId, MultipartFile voterImage, VoterDetails voterDetails) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        if (voterIdRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a Voter ID assigned");
        }

        VoterDetails cardDetails;

        if (voterIdRepository.findById(voterDetails.getVoterIDNumber()).isEmpty()) {
            if (voterImage != null && !voterImage.isEmpty()) {
                String fileName = fileService.uploadImage(path, voterImage);
                voterDetails.setUploadVoter(fileName);
            }

            voterDetails.setEmployee(employee);
            cardDetails = voterIdRepository.save(voterDetails);

        } else {
            VoterDetails existing = voterIdRepository.findById(voterDetails.getVoterIDNumber()).get();

            if (existing.getEmployee() == null) {
                existing.setEmployee(employee);
                cardDetails = voterIdRepository.save(existing);
            } else {
                throw new APIException("This Voter ID is already assigned to another employee");
            }
        }

        return cardDetails;
    }

    @Cacheable(value = "voter", key = "#employeeId")
    public VoterDTO getVoterByEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        VoterDetails voterDetails = employee.getVoterDetails();

        if (voterDetails == null) {
            throw new APIException("Voter ID details not found for this employee");
        }

        return modelMapper.map(voterDetails, VoterDTO.class);
    }

    @CachePut(value = "voter", key = "#employeeId")
    public VoterDetails updateVoter(String employeeId, MultipartFile voterImage, VoterDetails voterDetails) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        VoterDetails existing = employee.getVoterDetails();

        if (existing == null) {
            throw new APIException("Voter ID details not found for this employee");
        }

        if (!existing.getVoterIDNumber().equals(voterDetails.getVoterIDNumber())) {
            throw new APIException("Voter ID number cannot be changed once submitted");
        }

        if (voterImage != null && !voterImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, voterImage);
            existing.setUploadVoter(fileName);
        }

        existing.setFullName(voterDetails.getFullName());
        existing.setRelationName(voterDetails.getRelationName());
        existing.setGender(voterDetails.getGender());
        existing.setDateOfBirth(voterDetails.getDateOfBirth());
        existing.setAddress(voterDetails.getAddress());
        existing.setIssuedDate(voterDetails.getIssuedDate());

        return voterIdRepository.save(existing);
    }

    @CacheEvict(value = "voter", key = "#employeeId")
    public VoterDetails deleteByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        VoterDetails voterDetails = employee.getVoterDetails();

        if (voterDetails == null) {
            throw new APIException("Voter ID details not found for this employee" + employeeId);
        }
        employee.setVoterDetails(null);
        employeeRepository.save(employee);
        voterIdRepository.deleteById(voterDetails.getVoterIDNumber());
        return voterDetails;
    }
}