package com.backend.ems.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProjectDetailDto {
    private String fullName;
    private int userId;
    private String email;
    private String phone;
    private String projectName;

    public UserProjectDetailDto(String fullName, int userId, String email, String phone, String projectName) {
        this.fullName = fullName;
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.projectName = projectName;
    }

}
