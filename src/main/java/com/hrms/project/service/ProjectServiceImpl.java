package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.Project;
import com.hrms.project.entity.Team;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.ProjectNotFoundException;
import com.hrms.project.dto.ProjectDTO;
import com.hrms.project.repository.DepartmentRepository;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.ProjectRepository;
import com.hrms.project.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = {"project", "allProjects"})
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @CachePut(value = "project", key = "#result.projectId")
    @CacheEvict(value = "allProjects", key = "'all'")
    @Override
    public ProjectDTO saveProject(ProjectDTO projectDTO) {
        if(projectRepository.findById(projectDTO.getProjectId()).isPresent())
        {
            throw new APIException("Project already exists with ID "+projectDTO.getProjectId());
        }

        Project project = new Project();

        project.setProjectId(projectDTO.getProjectId());
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setProjectStatus(projectDTO.getProjectStatus());
        project.setProjectPriority(projectDTO.getProjectPriority());
        project.setClient(projectDTO.getClient());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());

        projectRepository.save(project);

        return projectDTO;
    }

    @Cacheable(value = "allProjects", key = "'all'")
    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        List<ProjectDTO> responseList=new ArrayList<>();

        for (Project project : projects) {
            ProjectDTO response=new ProjectDTO();

            response.setProjectId(project.getProjectId());
            response.setTitle(project.getTitle());
            response.setClient(project.getClient());
            response.setDescription(project.getDescription());
            response.setProjectStatus(project.getProjectStatus());
            response.setStartDate(project.getStartDate());
            response.setEndDate(project.getEndDate());
            response.setProjectPriority(project.getProjectPriority());


            responseList.add(response);

        }


        return responseList;


    }


    @Cacheable(value = "project", key = "#id")
    @Override
    public ProjectDTO getProjectById(String id) {
        Project project=projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException("Project not found with id "+id));

        ProjectDTO response=new ProjectDTO();
        response.setProjectId(project.getProjectId());
        response.setTitle(project.getTitle());
        response.setClient(project.getClient());
        response.setDescription(project.getDescription());
        response.setProjectStatus(project.getProjectStatus());

        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());

        response.setProjectPriority(project.getProjectPriority());




        return response;
    }



    @CachePut(value = "project", key = "#id")
    @CacheEvict(value = "allProjects", key = "'all'")
    @Override
    public ProjectDTO updateProject(String id, ProjectDTO projectDTO) {

        Project projectByID=projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException("Project not found with id "+id));

        projectByID.setProjectId(projectDTO.getProjectId());
        projectByID.setTitle(projectDTO.getTitle());
        projectByID.setClient(projectDTO.getClient());

        projectByID.setDescription(projectDTO.getDescription());

        projectByID.setProjectPriority(projectDTO.getProjectPriority());
        projectByID.setProjectStatus(projectDTO.getProjectStatus());
        projectByID.setStartDate(projectDTO.getStartDate());
        projectByID.setEndDate(projectDTO.getEndDate());

        Project savedProject = projectRepository.save(projectByID);

        projectRepository.save(savedProject);
        return projectDTO;
    }

    @Caching(evict= {
            @CacheEvict(value="project", key="#id"),
            @CacheEvict(value="allProjects", key="'all'")
    })
    @Override
    public ProjectDTO deleteProject(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id "+id));

        for (Employee emp : project.getEmployees()) {
            emp.getProjects().remove(project);
            employeeRepository.save(emp);
            evictProjectFromEmployeeCaches(emp.getEmployeeId());
        }
        project.getEmployees().clear();

        Team team = project.getTeam();
        if (team != null) {
            project.setTeam(null);
            evictProjectFromTeamCaches(team.getTeamId());
        }
//        if (team != null && team.getProjects() != null) {
//            team.getProjects().remove(project);
// we use this if we projects & teams are bi directional
//        }

        projectRepository.delete(project);
        return modelMapper.map(project, ProjectDTO.class);
    }

    @Autowired
    org.springframework.cache.CacheManager cacheManager;

    private void evictProjectFromEmployeeCaches(String employeeId){
        List<String> caches = List.of("achievement", "degree", "department", "departmentEmployees", "allDepartments", "employeeDepartment", "employee", "allEmployees", "contact", "allContact", "address", "allAddress", "primary", "job", "employeeTeams", "teamEmployees", "teamProjects");
        for (String cacheName : caches) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(employeeId);
            }
        }
    }

    private void evictProjectFromTeamCaches(String teamId) {
        List<String> caches = List.of( "department", "departmentEmployees", "allDepartments", "employeeDepartment", "employee", "allEmployees", "contact", "allContact", "allAddress", "primary", "job", "Images", "pan", "passport", "employeeTeams", "teamEmployees", "teamProjects");
        for (String cacheName : caches) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(teamId);
            }
        }
    }


}
