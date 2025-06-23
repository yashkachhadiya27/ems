package com.backend.ems.DTO;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectDto {
    private String projectName;
    private LocalDate projectJoinDate;
    private LocalDate projectEndDate;
    private int reportingToId;
    private String[] technologies;
    private int userId;

    public ProjectDto(String projectName, LocalDate projectJoinDate, LocalDate projectEndDate, int reportingToId,
            String[] technologies, int userId) {
        this.projectName = projectName;
        this.projectJoinDate = projectJoinDate;
        this.projectEndDate = projectEndDate;
        this.reportingToId = reportingToId;
        this.technologies = technologies;
        this.userId = userId;
    }

}
