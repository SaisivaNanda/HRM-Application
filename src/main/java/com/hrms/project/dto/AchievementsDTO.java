package com.hrms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchievementsDTO implements Serializable {
    private String certificationName;
    private String issuingAuthorityName;
    private String certificationURL;
    private String issueMonth;
    private String issueYear;
    private String expirationMonth;
    private String expirationYear;

    private String licenseNumber;
    private String achievementFile;

}
