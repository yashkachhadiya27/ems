package com.backend.ems.DTO;

import lombok.Data;

@Data
public class DepartmentEmployeeCountDTO {
    private String department;
    private long employeeCount;

    public DepartmentEmployeeCountDTO(String department, long employeeCount) {
        this.department = department;
        this.employeeCount = employeeCount;
    }

}
