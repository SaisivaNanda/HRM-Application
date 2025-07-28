package com.hrms.project.controller;


import com.hrms.project.dto.PublicEmployeeDetails;
import com.hrms.project.service.PublicEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PublicController {

    @Autowired
    private PublicEmployeeService publicEmployeeService;

    @GetMapping("public/employees")
    public ResponseEntity<List<PublicEmployeeDetails>> getAllEmployees() {

        return new ResponseEntity<>(publicEmployeeService.getAllEmployees(), HttpStatus.OK);

    }
        @GetMapping("public/{employeeId}/employee/details")
                public ResponseEntity<PublicEmployeeDetails> getEmployeeDetails(@PathVariable String employeeId)
        {
            return new ResponseEntity<>(publicEmployeeService.getEmployeeDetails(employeeId),HttpStatus.OK);
        }

        @GetMapping("/{employeeId}/header")
    public  ResponseEntity<PublicEmployeeDetails> getHeaderDetails(@PathVariable String employeeId)
        {
            return new ResponseEntity<>(publicEmployeeService.getHeaderDetails(employeeId),HttpStatus.OK);
        }



}
