package com.backend.ems.DTO;

import com.backend.ems.Entity.Salary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmpDetailsSalaryDTO {
    private int id;
    private String image;
    private String fullName;
    private String email;
    private String department;
    private Salary salary;

    public EmpDetailsSalaryDTO(int id, String image, String fullName, String email, String department) {
        this.id = id;
        this.image = image;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }

}