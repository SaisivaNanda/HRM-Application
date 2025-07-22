package com.hrms.project.controller;

import com.hrms.project.entity.Team;
import com.hrms.project.payload.EmployeeTeamDTO;
import com.hrms.project.payload.TeamController;
import com.hrms.project.payload.TeamResponse;
//import com.hrms.project.service.TeamService;
import com.hrms.project.service.TeamServiceImpl;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class EmployeeTeamController {

    @Autowired
    private TeamServiceImpl teamService;


    @PostMapping("/team")
    public ResponseEntity<TeamController> createTeam(@Valid @RequestBody TeamController teamController) {
        TeamController createdTeam = teamService.saveTeam(teamController);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }


    @GetMapping("/employee/team/{employeeId}")
    public ResponseEntity<List<TeamResponse>> getAllEmployees(@PathVariable String employeeId) {
        List<TeamResponse> teamList = teamService.getTeamAllEmployees(employeeId);
        return ResponseEntity.ok(teamList);
   }

   @GetMapping("/team/employee/{teamId}")
   public ResponseEntity<List<TeamResponse>> getTeamById(@PathVariable String teamId) {
        List<TeamResponse> employeeList = teamService.getAllTeamEmployees(teamId);
        return ResponseEntity.ok(employeeList);
   }



    @PutMapping("/team/employee/{teamId}")
    public ResponseEntity<String> updateTeam(@PathVariable String teamId, @Valid @RequestBody TeamController teamDTO) {
        return new ResponseEntity<>(teamService.UpdateTeam(teamId,teamDTO), HttpStatus.OK);
    }

    @GetMapping("/{teamId}/team/employee")
    public ResponseEntity<EmployeeTeamDTO> getEmployeeByTeamId(@PathVariable String teamId) {
        return new ResponseEntity<>(teamService.getEmployeeByTeamId(teamId),HttpStatus.OK);



    }

    @GetMapping("/team/projects/{teamId}")
    public ResponseEntity<List<String>> getProjectsByTeam(@PathVariable String teamId) {
       return new ResponseEntity<>(teamService.getProjectsByTeam(teamId),HttpStatus.OK) ;


    }


}