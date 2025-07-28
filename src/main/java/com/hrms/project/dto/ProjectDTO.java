package com.hrms.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO implements Serializable {


    @NotBlank(message = "Project Id is required")
    @Pattern(regexp = "^PRO\\d{4}$",message = "Project name must start time PRO and followed by 4 digits")
    private String projectId;
    private String title;
    private String client;
    private String description;
    private String projectPriority;
    private String projectStatus;
    private LocalDate startDate;
    private LocalDate endDate;

}
