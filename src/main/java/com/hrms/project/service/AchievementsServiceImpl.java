package com.hrms.project.service;

import com.hrms.project.dto.SkillsDTO;
import com.hrms.project.entity.Achievements;

import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.AchievementsDTO;
import com.hrms.project.repository.AchievementsRepository;
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
@CacheConfig(cacheNames = {"achievement", "certifications", "skills"})
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
    public AchievementsDTO addAchievements(String employeeId,MultipartFile image, AchievementsDTO achievementsDTO) throws IOException {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        Achievements newAchievements = new Achievements();

        modelMapper.map(achievementsDTO,newAchievements);

        newAchievements.setEmployee(employee);


        if (image != null && !image.isEmpty()) {
            String img = fileService.uploadImage(path, image);
            newAchievements.setAchievementFile(img);
        }
        AchievementsDTO achievements=modelMapper.map(achievementsRepository.save(newAchievements),AchievementsDTO.class);
        return achievements;
    }

    @Cacheable(value = "certifications", key = "#employeeId")
    public List<AchievementsDTO> getCertifications(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        List<Achievements> achievements=employee.getAchievements();

        List<AchievementsDTO> details=achievements.stream().map(achieve->
        {
            return modelMapper.map(achieve,AchievementsDTO.class);
        }).toList();

        return details;

    }

    @Cacheable(value = "achievement", key = "#employeeId + '::' + #achievementId")
    public AchievementsDTO getAchievement(String employeeId,Long achievementId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        Achievements achievements=achievementsRepository.findById(achievementId)
                .orElseThrow(()->new APIException("Achievement not found with id " + achievementId));

        return modelMapper.map(achievements,AchievementsDTO.class);

    }


    @CachePut(value = "achievement", key = "#employeeId + '::' + #certificateId")
    public AchievementsDTO updateAchievements(String employeeId, Long certificateId,
                                              MultipartFile image, AchievementsDTO achievementsDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        Achievements achievementToUpdate = employee.getAchievements().stream()
                .filter(achieve -> achieve.getId().equals(certificateId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Achievement with certificate ID " + certificateId + " not found."));

        modelMapper.map(achievementsDTO, achievementToUpdate);

        if (image != null && !image.isEmpty()) {
            String img = fileService.uploadImage(path, image);
            achievementToUpdate.setAchievementFile(img);
        }
       achievementsRepository.save(achievementToUpdate);

        return modelMapper.map(achievementToUpdate, AchievementsDTO.class);
    }

    @CacheEvict(value = "achievement", key = "#employeeId + '::' + #certificateId")
    public AchievementsDTO deleteAchievements(String employeeId, Long certificateId) {


        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        List<Achievements> achievements=employee.getAchievements();

        Achievements achievementToDelete = achievements.stream()
                .filter(achieve -> achieve.getId().equals(certificateId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Achievement with certificate ID " + certificateId + " not found."));

        achievementsRepository.delete(achievementToDelete);

        return modelMapper.map(achievementToDelete, AchievementsDTO.class);

    }

    @CachePut(value = "skills", key = "#employeeId")
    public SkillsDTO addSkills(String employeeId, SkillsDTO resumeDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        if (employee.getSkills() != null && !employee.getSkills().isEmpty()) {
            throw new APIException("Skills already exist for this employee cannot add again.");
        }
        modelMapper.map(resumeDTO,employee);
        employeeRepository.save(employee);
        return modelMapper.map(employee, SkillsDTO.class);

    }

    @CachePut(value = "skills", key = "#employeeId")
    public SkillsDTO updateSkills(String employeeId, SkillsDTO resumeDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        modelMapper.map(resumeDTO,employee);
        employeeRepository.save(employee);
        return modelMapper.map(employee, SkillsDTO.class);

    }

    @Cacheable(value = "skills", key = "#employeeId")
    public SkillsDTO getSkills(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        SkillsDTO resumeDTO = modelMapper.map(employee, SkillsDTO.class);
        resumeDTO.setSkills(employee.getSkills());

        return resumeDTO;
    }

    @CacheEvict(value = "skills", key = "#employeeId")
    public SkillsDTO deleteSkills(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        if (employee.getSkills() == null || employee.getSkills().isEmpty()) {
            throw new APIException("No skills found to delete for this employee.");
        }

        employee.setSkills(null);
        employeeRepository.save(employee);

        SkillsDTO resumeDTO = modelMapper.map(employee, SkillsDTO.class);
        resumeDTO.setSkills(employee.getSkills());

        return resumeDTO;
    }


}
