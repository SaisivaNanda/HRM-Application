package com.hrms.project.service;

import com.hrms.project.entity.DegreeCertificates;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.DegreeDTO;
import com.hrms.project.repository.DegreeCertificatesRepository;
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
import java.util.List;


@Service
@CacheConfig(cacheNames = "degree")
public class DegreeServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private DegreeCertificatesRepository degreeCertificatesRepository;

    @Value("${project.image}")
    private String path;

    @CachePut(value = "degree", key = "#employeeId")
    public DegreeCertificates addDegree(String employeeId, MultipartFile addFiles, DegreeCertificates degree) throws IOException {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with id " + employeeId));


        if (addFiles != null && !addFiles.isEmpty()) {
            String fileName = fileService.uploadImage(path, addFiles);
            degree.setAddFiles(fileName);
        }
        degree.setEmployee(employee);
        return  degreeCertificatesRepository.save(degree);
    }

    @Cacheable(value = "degree", key = "#employeeId")
    public List<DegreeDTO> getDegree(String employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        List<DegreeCertificates> degreeCertificates =employee.getDegreeCertificates();
        if (degreeCertificates == null) {
            throw new APIException("Degree certificates doesn't exist");
        }
        return degreeCertificates.stream()
                .map(d -> modelMapper
                        .map(d, DegreeDTO.class)).toList();
    }


    @CachePut(value = "degree", key = "#employeeId")
    public DegreeCertificates updateDegree(String employeeId, MultipartFile image, DegreeCertificates updatedDegree) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        DegreeCertificates existing = degreeCertificatesRepository.findById(updatedDegree.getId())
                .orElseThrow(() -> new APIException("Certificate with id " + updatedDegree.getId() + " not found"));

        if (!existing.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This degree does not belong to employee with ID: " + employeeId);
        }

        String oldFile = existing.getAddFiles();

        modelMapper.map(updatedDegree, existing);
        existing.setId(updatedDegree.getId());
        existing.setEmployee(employee);

        if (image != null && !image.isEmpty()) {
            String fileName = fileService.uploadImage(path, image);
            existing.setAddFiles(fileName);
        } else {
            existing.setAddFiles(oldFile);
        }

        return degreeCertificatesRepository.save(existing);
    }

    @CacheEvict(value = "degree", key = "#employeeId")
    public DegreeCertificates deleteById(String employeeId, Long id) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EmployeeNotFoundException("Employee not found"));

        DegreeCertificates degreeCertificates=degreeCertificatesRepository.findById(id)
                .orElseThrow(()-> new APIException("Degree certificate not found with ID: " + id));

        if (!degreeCertificates.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This degree certificate does not belong to the given employee.");
        }
        degreeCertificatesRepository.deleteById(id);

        return degreeCertificates;
    }
}