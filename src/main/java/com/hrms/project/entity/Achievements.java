package com.hrms.project.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Achievements {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String certificationName;
    private String issuingAuthorityName;
    private String certificationURL;
    private String issueMonth;
    private String issueYear;
    private String expirationMonth;
    private String expirationYear;
    private String licenseNumber;
    private String achievementFile;

    @ManyToOne
    @JoinColumn(name="employee_id")
    @JsonBackReference
    private Employee employee;








}
