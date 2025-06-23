package com.backend.ems.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmployeesDTO {
    private String image;
    private String fullName;
    private String email;
    private String gender;
    private String phone;
    private String department;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    public EmployeesDTO(String image, String fullName, String email, String gender, String phone, String department,
            LocalDate dateOfBirth, LocalDate dateOfJoining) {
        this.image = image;
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.department = department;
        this.dateOfBirth = dateOfBirth;
        this.dateOfJoining = dateOfJoining;
    }
    

}
