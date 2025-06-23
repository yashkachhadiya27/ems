package com.backend.ems.DTO;

import lombok.Data;

@Data
public class BirthdayDto {
    private String fullname;
    private String email;

    public BirthdayDto(String fullname, String email) {
        this.fullname = fullname;
        this.email = email;
    }

}
