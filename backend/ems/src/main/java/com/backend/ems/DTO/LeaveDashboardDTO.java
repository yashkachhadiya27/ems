package com.backend.ems.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveDashboardDTO {
    private int remainingLeaves;
    private int wfhLeavesRemaining;
    private int remainingRestrictedLeaves;
    private int currentQuarterLeavesUsed;
    private int totalLeaves;
}
