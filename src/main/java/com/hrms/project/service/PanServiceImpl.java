package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.PanDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.PanDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.PanDetailsRepository;
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
@CacheConfig(cacheNames = "pan")
public class PanServiceImpl {
    @Autowired
    private PanDetailsRepository panDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @CachePut(value = "pan", key = "#employeeId")
    public PanDTO createPan(String employeeId, MultipartFile panImage, PanDetails panDetails) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (panDetailsRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a PAN assigned");
        }
        Optional<PanDetails> existingPan = panDetailsRepository.findById(panDetails.getPanNumber());

        if (existingPan.isPresent()) {

            PanDetails pan = existingPan.get();
            if (pan.getEmployee() == null) {
                pan.setEmployee(employee);

                return modelMapper.map(panDetailsRepository.save(pan), PanDTO.class);
            } else {
                throw new APIException("This PAN is already assigned to another employee");
            }
        }

        panDetails.setEmployee(employee);
        if (panImage != null && !panImage.isEmpty()) {
            String image = fileService.uploadImage(path, panImage);
            panDetails.setPanImage(image);
        }

        return modelMapper.map(panDetailsRepository.save(panDetails), PanDTO.class);
    }

    @Cacheable(value = "pan", key = "#employeeId")
    public PanDTO getPanDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PanDetails details=employee.getPanDetails();

        if(details!=null)
        {
            PanDTO panDTO=modelMapper.map(details,PanDTO.class);
            panDTO.setEmployeeId(employeeId);
            return panDTO;
        }
        else
        {
            throw new APIException("This employee does not have a PAN assigned");
        }

    }

    @CachePut(value = "pan", key = "#employeeId")
    public PanDetails UpdatePanDetails(String employeeId, MultipartFile panImage,PanDetails panDetails)  throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PanDetails existing = employee.getPanDetails();
        if (existing == null) {
            throw new APIException("PAN Details not found for employee: " + employeeId);
        }

        if (!existing.getPanNumber().equals(panDetails.getPanNumber())) {
            throw new APIException("Pan number cannot be changed once submitted");
        }

        if (panImage != null && !panImage.isEmpty()) {
            String image = fileService.uploadImage(path, panImage);
            existing.setPanImage(image);
        }
        existing.setPanName(panDetails.getPanName());
        existing.setDateOfBirth(panDetails.getDateOfBirth());
        existing.setParentsName(panDetails.getParentsName());
        existing.setPanNumber(panDetails.getPanNumber());

        PanDetails updated=panDetailsRepository.save(existing);
        return modelMapper.map(updated, PanDetails.class);
    }

    @CacheEvict(value = "pan", key = "#employeeId")
    public PanDetails deletePanByEmployeeId(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PanDetails panDetails=employee.getPanDetails();
        if(panDetails==null){
            throw  new APIException("PAN details not found for employeeId"+ employeeId);
        }
        employee.setPanDetails(null);
        employeeRepository.save(employee);
        return  panDetails;
    }
}