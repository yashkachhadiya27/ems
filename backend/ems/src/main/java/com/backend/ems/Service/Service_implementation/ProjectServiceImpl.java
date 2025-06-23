package com.backend.ems.Service.Service_implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.ems.DTO.ProjectDto;
import com.backend.ems.DTO.UserProjectDetailDto;
import com.backend.ems.Entity.Project;
import com.backend.ems.Entity.Register;
import com.backend.ems.Exception.EmployeeNotFoundException;
import com.backend.ems.Repository.ProjectRepository;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.Service_Interface.ProjectServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectServiceInterface {
    private final ProjectRepository projectRepository;
    private final RegisterRepository registerRepository;

    @Override
    public void addProject(ProjectDto projectDto) {
        Project project = new Project();
        project.setProjectName(projectDto.getProjectName());
        project.setProjectJoinDate(projectDto.getProjectJoinDate());
        project.setProjectEndDate(projectDto.getProjectEndDate());
        project.setReportingToId(projectDto.getReportingToId());
        project.setTechnologies(projectDto.getTechnologies());
        Project savedProject = projectRepository.save(project);
        Register user = registerRepository.findById(projectDto.getUserId())
                .orElseThrow(() -> new EmployeeNotFoundException("User not found"));
        user.getProjects().add(savedProject);
        registerRepository.save(user);

    }

    private ProjectDto convertToDto(Project project, int userId) {
        ProjectDto dto = new ProjectDto();
        dto.setProjectName(project.getProjectName());
        dto.setProjectJoinDate(project.getProjectJoinDate());
        dto.setProjectEndDate(project.getProjectEndDate());
        dto.setReportingToId(project.getReportingToId());
        dto.setTechnologies(project.getTechnologies());
        dto.setUserId(userId);
        return dto;
    }

    @Override
    public List<ProjectDto> getProjectsByUserId(int userId) {
        return projectRepository.findByUsersId(userId);
    }

    @Override
    public List<UserProjectDetailDto> getEmployeesInProject(int id) {
        return projectRepository.findByReportingToId(id);
    }

}
