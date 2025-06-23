package com.backend.ems.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.ProjectDto;
import com.backend.ems.DTO.UserProjectDetailDto;
import com.backend.ems.Entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("SELECT new com.backend.ems.DTO.ProjectDto(p.projectName, p.projectJoinDate, p.projectEndDate, p.reportingToId, p.technologies,p.id) "
            +
            "FROM Project p JOIN p.users u WHERE u.id = :employeeId")
    List<ProjectDto> findByUsersId(int employeeId);

    @Query("SELECT new com.backend.ems.DTO.UserProjectDetailDto(CONCAT(u.fname,' ',u.lname),u.id,u.email,u.phone,p.projectName) "
            +
            "FROM Project p JOIN p.users u WHERE p.reportingToId = :id")
    List<UserProjectDetailDto> findByReportingToId(int id);
}
