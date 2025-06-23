package com.backend.ems.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AveragesDto {
    private Double avgTotalTime;
    private Double avgBreakTime;
    private Double avgWorkingTime;

}
