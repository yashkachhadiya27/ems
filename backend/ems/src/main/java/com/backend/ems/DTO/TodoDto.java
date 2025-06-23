package com.backend.ems.DTO;

import com.backend.ems.Enums.TodoStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TodoDto {
    private String description;
    private TodoStatus status;

    public TodoDto(String description, TodoStatus status) {
        this.description = description;
        this.status = status;
    }

}
