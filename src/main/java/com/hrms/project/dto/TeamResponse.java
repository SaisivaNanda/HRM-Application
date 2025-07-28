package com.hrms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse implements Serializable{

    private String teamId;
    private String teamName;
    private List<EmployeeTeamResponse> employees;

}
