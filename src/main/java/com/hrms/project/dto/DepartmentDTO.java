package com.hrms.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDTO implements Serializable {

    @NotBlank(message="DepartmentId is required")
    @Pattern(regexp = "^DEP\\d{3}$",message = "departmentId  should start with DEP followed by 3 digits")
    private String departmentId;
    private String departmentName;
    private String departmentDescription;
}
