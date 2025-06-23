package com.backend.ems.DTO;

import lombok.Data;

@Data
public class FullNameDto {
    private int id;
    private String fullName;

    public FullNameDto(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
}
