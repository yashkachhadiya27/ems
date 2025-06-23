package com.backend.ems.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponseDTO {
    private String image;
    private String fullName;
    private String email;
    private String department;
    private int id;
    private String leaveType;
    private String reason;
    private LocalDate leaveFromDate;
    private LocalDate leaveToDate;
    private String status;
}
