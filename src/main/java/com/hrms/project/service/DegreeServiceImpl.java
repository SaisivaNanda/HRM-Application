package com.hrms.project.service;

import com.hrms.project.entity.DegreeCertificates;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.payload.DegreeDTO;
import com.hrms.project.repository.DegreeCertificatesRepository;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
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

        List<DegreeCertificates> degreeCertificates = employee.getDegreeCertificates();
        if (degreeCertificates == null || degreeCertificates.isEmpty()) {
            throw new APIException("Degree certificates don't exist");
        }

        DegreeCertificates certificate = degreeCertificates.stream()
                .filter(dept -> dept.getId().equals(updatedDegree.getId()))
                .findFirst()
                .orElseThrow(() -> new APIException("Certificate with id " + updatedDegree.getId()+ " not found"));

        certificate.setDegree(updatedDegree.getDegree());
        certificate.setBranchOrSpecialization(updatedDegree.getBranchOrSpecialization());
        certificate.setCgpaOrPercentage(updatedDegree.getCgpaOrPercentage());
        certificate.setUniversityOrCollege(updatedDegree.getUniversityOrCollege());
        certificate.setYearOfCompletion(updatedDegree.getYearOfCompletion());
        certificate.setYearOfJoining(updatedDegree.getYearOfJoining());


        if (image != null && !image.isEmpty()) {
            String fileName = fileService.uploadImage(path, image);
            certificate.setAddFiles(fileName);
        }

        return degreeCertificatesRepository.save(certificate);
    }

}