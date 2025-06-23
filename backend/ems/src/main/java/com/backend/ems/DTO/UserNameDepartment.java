package com.backend.ems.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class UserNameDepartment {
    private String image;
    private String name;
    private String department;
    public UserNameDepartment(String image,String name, String department) {
        this.image=image;
        this.name = name;
        this.department = department;
    }
    
}
