package com.hrms.project.service;

import com.hrms.project.entity.Achievements;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.repository.AchievementsRepository;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class AchievementsServiceImpl {

    @Autowired
    private AchievementsRepository achievementsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;
    @Autowired
    private ModelMapper modelMapper;

    @CachePut(value = "achievement", key = "#employeeId")
    public Achievements addAchievements(String employeeId,MultipartFile image, Achievements achievements) throws IOException {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        Achievements newAchievements = new Achievements();

        modelMapper.map(image,newAchievements);

        newAchievements.setEmployee(employee);


        if (image != null && !image.isEmpty()) {
            String img = fileService.uploadImage(path, image);
            employee.setEmployeeImage(img);
        }

        return achievementsRepository.save(newAchievements);
    }
}
