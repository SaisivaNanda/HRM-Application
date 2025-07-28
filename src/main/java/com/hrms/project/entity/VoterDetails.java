package com.hrms.project.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "employee")
@ToString(exclude = "employee")
public class VoterDetails {

    @Id
    private String voterIDNumber;
    private String fullName;
    private String relationName;
    private String gender;
    private LocalDate dateOfBirth;
    private String Address;
    private LocalDate issuedDate;
    private String uploadVoter;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employeeId")
    @JsonBackReference
    private Employee employee;

}