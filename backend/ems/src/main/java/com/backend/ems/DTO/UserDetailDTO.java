package com.backend.ems.DTO;

import java.time.LocalDate;
import java.util.List;

import com.backend.ems.Entity.Address;
import com.backend.ems.Entity.Experience;

import lombok.Data;

@Data
public class UserDetailDTO {

    private String fname;

    private String mname;

    private String lname;

    private String email;

    private String gender;

    private String image;

    private String department;

    private LocalDate dateOfJoining;

    private LocalDate dateOfBirth;

    private String phone;

    private String[] skills;

    private Address address;

    private List<Experience> experience;
}
