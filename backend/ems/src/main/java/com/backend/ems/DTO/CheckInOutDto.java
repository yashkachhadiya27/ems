package com.backend.ems.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckInOutDto {
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
