package com.backend.ems.DTO;

import lombok.Data;

@Data
public class WorkAnnivesariesDto {
    private String fullname;
    private String email;

    public WorkAnnivesariesDto(String fullname, String email) {
        this.fullname = fullname;
        this.email = email;
    }
}
