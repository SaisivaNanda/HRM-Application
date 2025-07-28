package com.hrms.project.service;


import com.hrms.project.dto.AssignmentDTO;
import com.hrms.project.entity.Assignment;
import com.hrms.project.entity.Employee;
import com.hrms.project.entity.Project;
import com.hrms.project.entity.Team;
import com.hrms.project.handlers.AssignmentNotFound;
import com.hrms.project.handlers.ProjectNotFoundException;
import com.hrms.project.handlers.TeamNotFoundException;
import com.hrms.project.repository.AssignmentRepository;

import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.ProjectRepository;
import com.hrms.project.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {"assignments"})
public class AssignmentServiceImpl {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ModelMapper modelMapper;

    @CacheEvict(value = "assignments", key = "#assignmentDTO.employeeId")
    public String createAssignment(AssignmentDTO assignmentDTO) {

        Employee employee = employeeRepository.findById(assignmentDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee with id: " + assignmentDTO.getEmployeeId() + " not found"));

        Project project = projectRepository.findById(assignmentDTO.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with id: " + assignmentDTO.getProjectId() + " not found"));

        Team team=teamRepository.findById(assignmentDTO.getAssignedBy())
                .orElseThrow(() -> new TeamNotFoundException("Team with id: " + assignmentDTO.getAssignedBy() + " not found"));


        Assignment assignment = new Assignment();

        assignment.setAssignmentName(assignmentDTO.getAssignmentName());
        assignment.setAssignmentDescription(assignmentDTO.getAssignmentDescription());
        assignment.setAssignedBy(team.getTeamId());
        assignment.setStatus(assignmentDTO.getStatus());
        assignment.setPriority(assignmentDTO.getPriority());
        assignment.setStartDate(assignmentDTO.getStartDate());

        assignment.setDueDate(assignmentDTO.getDueDate());
        assignment.setRemark(assignmentDTO.getRemark());
        assignment.setRating(assignmentDTO.getRating());
        assignment.setEmployee(employee);
        assignment.setProject(project);

        assignmentRepository.save(assignment);
        return "Assignment created successfully";
    }


    @Cacheable(value = "assignments", key = "#employeeId")
    public List<AssignmentDTO> getAllAssignments(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new EntityNotFoundException("Employee with id: " + employeeId + " not found"));

        List<Assignment> assignment = employee.getAssignments();
       return assignment.stream()
                .map(a ->
                        modelMapper.map(a, AssignmentDTO.class)).toList();

    }

    @Cacheable(value = "assignments", key = "#employeeId + '::' + #assignmentId")
    public AssignmentDTO getAssignment(String employeeId, Long assignmentId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new EntityNotFoundException("Employee with id: " + employeeId + " not found"));

        List<Assignment> assignment = employee.getAssignments();
        Assignment assign= assignment.stream()
                .filter(a -> a.getAssignmentId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new AssignmentNotFound("Assignment Not Found with Id: " + assignmentId + " not found"));

        return modelMapper.map(assign, AssignmentDTO.class);

    }


    @Caching(evict = {
            @CacheEvict(value = "assignments", key = "#assignmentDTO.employeeId"),  },
            put = {
                    @CachePut(value = "assignments", key = "#assignmentDTO.employeeId + '::' + #assignmentId")  })
    public String updateAssignment(Long assignmentId, AssignmentDTO assignmentDTO) {

        Employee employee = employeeRepository.findById(assignmentDTO.getEmployeeId()).orElseThrow(() ->
                new EntityNotFoundException("Employee with id: " + assignmentDTO.getEmployeeId() + " not found"));

        Project project = projectRepository.findById(assignmentDTO.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with id: " + assignmentDTO.getProjectId() + " not found"));

        Team team=teamRepository.findById(assignmentDTO.getAssignedBy())
                .orElseThrow(() -> new TeamNotFoundException("Team with id: " + assignmentDTO.getAssignedBy() + " not found"));


        List<Assignment> assignments = employee.getAssignments();
       Assignment assign= assignments.stream()
                .filter(a -> a.getAssignmentId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new AssignmentNotFound("Assignment Not Found with Id: " +assignmentId + " not found"));

        assign.setAssignmentName(assignmentDTO.getAssignmentName());
        assign.setAssignmentDescription(assignmentDTO.getAssignmentDescription());
        assign.setAssignedBy(team.getTeamId());
        assign.setStatus(assignmentDTO.getStatus());
        assign.setPriority(assignmentDTO.getPriority());
        assign.setStartDate(assignmentDTO.getStartDate());

        assign.setDueDate(assignmentDTO.getDueDate());
        assign.setRemark(assignmentDTO.getRemark());
        assign.setRating(assignmentDTO.getRating());

        assign.setEmployee(employee);
        assign.setProject(project);

       assignmentRepository.save(assign);


        return "Assignment updated successfully";
    }

    @Caching(evict = {
            @CacheEvict(value = "assignments", allEntries = true)})
    public String deleteAssignment(Long assignmentId) {

        Assignment assignment=assignmentRepository.findById(assignmentId)
                .orElseThrow(()->new AssignmentNotFound("Assignment not found with id: "+assignmentId));

        assignmentRepository.delete(assignment);
        return "Assignment deleted successfully";
    }
}
