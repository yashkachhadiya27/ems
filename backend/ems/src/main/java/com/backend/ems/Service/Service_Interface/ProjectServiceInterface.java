package com.backend.ems.Service.Service_Interface;

import java.util.List;

import com.backend.ems.DTO.ProjectDto;
import com.backend.ems.DTO.UserProjectDetailDto;

public interface ProjectServiceInterface {
    public void addProject(ProjectDto projectDto);

    public List<ProjectDto> getProjectsByUserId(int userId);

    public List<UserProjectDetailDto> getEmployeesInProject(int id);
}
