package com.backend.ems.DTO;

import java.time.LocalDate;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class AttendanceDto {
    private LocalDate attendanceDate;
    private Long totalInTime;
    private Long totalBreakTime;

    public AttendanceDto(LocalDate attendanceDate, Long totalInTime, Long totalBreakTime) {
        this.attendanceDate = attendanceDate;
        this.totalInTime = totalInTime;
        this.totalBreakTime = totalBreakTime;
    }

}
