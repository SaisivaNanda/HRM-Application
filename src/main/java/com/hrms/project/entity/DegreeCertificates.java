package com.hrms.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "employee")
@ToString(exclude = "employee")
public class DegreeCertificates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String degree;
    private String branchOrSpecialization;
    private LocalDate startMonth;
    private LocalDate endMonth;
    private LocalDate startYear;
    private LocalDate endYear;
    private String cgpaOrPercentage;
    private String universityOrCollege;
    private String addFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonBackReference
    private Employee employee;

}