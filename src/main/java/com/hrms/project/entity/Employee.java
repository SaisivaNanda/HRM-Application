package com.hrms.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {
        "teams", "projects", "department","voterDetails",
        "panDetails", "drivingLicense", "passportDetails", "aadhaarCardDetails","degreeCertificates","assignments"
})
@ToString(exclude = {
        "teams", "projects", "department","voterDetails",
        "panDetails", "drivingLicense", "passportDetails", "aadhaarCardDetails","degreeCertificates","assignments"
})
public class Employee {

    @Id
    private String employeeId;

    private String firstName;
    private String middleName;
    private String lastName;
    private String displayName;
    private String maritalStatus;
    private String bloodGroup;
    private String physicallyHandicapped;
    private String nationality;
    private String jobTitlePrimary;
    private String jobTitleSecondary;
    private String gender;
    private String workEmail;
    private String personalEmail;
    private String mobileNumber;
    private String workNumber;
    private String street;
    private String city;
    private String state;
    private String district;
    private String zip;
    private String country;

    private LocalDateTime shiftStartTime;
    private LocalDateTime shiftEndTime;

    private String employeeImage;

    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String inProbation;
    private LocalDate probationStartDate;
    private LocalDate probationEndDate;
    private String probationPolicy;
    private String workingType;
    private String timeType;
    private String contractStatus;
    private LocalDate contractStartDate;

    private List<String> skills;
    private String location;

    @ManyToMany(mappedBy = "employees")
    private List<Team> teams = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "employee_project",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects = new ArrayList<>();

    @ManyToOne(optional = true)
    @JoinColumn(name = "department_id", nullable = true)
    @JsonBackReference
    private Department department;


    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private AadhaarCardDetails aadhaarCardDetails;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private PanDetails panDetails;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private DrivingLicense drivingLicense;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private PassportDetails passportDetails;

    @OneToOne(mappedBy = "employee",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private VoterDetails voterDetails;

    @OneToMany(mappedBy="employee",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DegreeCertificates> degreeCertificates;

    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkExperienceDetails>workExperienceDetails;



    @OneToMany(mappedBy="employee",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Achievements> achievements;

    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Assignment> assignments;

//    @OneToMany(mappedBy = "employee")
//    @JsonManagedReference
//    private List<Skills> skills;



}





