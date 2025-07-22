package com.hrms.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoterDTO {

    private String fullName;
    private String relationName;
    private String gender;
    private LocalDate dateOfBirth;
    private String Address;
    private LocalDate issuedDate;

}
