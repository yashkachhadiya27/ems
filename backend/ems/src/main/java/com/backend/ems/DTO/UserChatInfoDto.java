package com.backend.ems.DTO;

import com.backend.ems.Enums.UserStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserChatInfoDto {
    private int id;
    private String image;
    private String name;
    private String department;
    private UserStatus status;

    public UserChatInfoDto(int id, String image, String name, String department, UserStatus status) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.department = department;
        this.status = status;
    }

}
