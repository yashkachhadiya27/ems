package com.backend.ems.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDto {
    private String fullName;
    private String image;
    private String leaveType;
    private LocalDate leaveFromDate;
    private LocalDate leaveToDate;
}
