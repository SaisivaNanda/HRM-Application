package com.hrms.project.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "employee")
@ToString(exclude = "employee")

public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    private String assignmentName;
    private String assignmentDescription;
    private String assignedBy;
    private String status;
    private String priority;

    private LocalDate  startDate;
    private LocalDate  dueDate;
    private Integer rating;
    private String remark;

    @ManyToOne
    @JoinColumn(name="employee_id")
     @JsonBackReference
    private Employee employee;

    @ManyToOne
    @JoinColumn(name="project_id")
    @JsonBackReference
    private Project project;

}
