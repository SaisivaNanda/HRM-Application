package com.hrms.project.controller;


import com.hrms.project.dto.AssignmentDTO;
import com.hrms.project.entity.Assignment;
import com.hrms.project.service.AssignmentServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AssignmentController {


    @Autowired
    private AssignmentServiceImpl assignmentService;

    @PostMapping("/assignments")
    public ResponseEntity<String> createAssignment( @RequestBody AssignmentDTO assignmentDTO){

        return new ResponseEntity<>(assignmentService.createAssignment(assignmentDTO), HttpStatus.CREATED);

    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments(@PathVariable String employeeId){

        return new ResponseEntity<>(assignmentService.getAllAssignments(employeeId),HttpStatus.OK);

    }
    @GetMapping("/{employeeId}/{assignmentId}")
    public ResponseEntity<AssignmentDTO> getAssignment(@PathVariable String employeeId, @PathVariable Long assignmentId){
        return new ResponseEntity<>(assignmentService.getAssignment(employeeId,assignmentId),HttpStatus.OK);
    }

    @PutMapping("/{assignmentId}")
    public ResponseEntity<String> updateAssignment(@PathVariable  Long assignmentId,
                                                   @RequestBody AssignmentDTO assignmentDTO){
        return new ResponseEntity<>(assignmentService.updateAssignment(assignmentId,assignmentDTO),HttpStatus.CREATED);
    }
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<String> deleteAssignment(@PathVariable  Long assignmentId){
        return new ResponseEntity<>(assignmentService.deleteAssignment(assignmentId),HttpStatus.OK);
    }

}
