package com.hrms.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDetailsDTO implements Serializable {

    private String workEmail;
    private String personalEmail;
    private String mobileNumber;
    private String workNumber;
}
