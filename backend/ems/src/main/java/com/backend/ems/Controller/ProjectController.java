package com.backend.ems.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.DTO.ProjectDto;
import com.backend.ems.DTO.UserProjectDetailDto;
import com.backend.ems.Entity.Project;
import com.backend.ems.Service.Service_implementation.ProjectServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectServiceImpl projectServiceImpl;

    @PostMapping("/employee/addProject")
    public ResponseEntity<CustomResponse> addProject(@RequestBody ProjectDto projectDTO) {
        try {
            projectServiceImpl.addProject(projectDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResponse("Add Successfully!", 201));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResponse("Internal server error!", 500));
        }
    }

    @GetMapping("/employee/getProject/{userId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByUserId(@PathVariable int userId) {
        return ResponseEntity.status(200).body(projectServiceImpl.getProjectsByUserId(userId));
    }

    @GetMapping("/employee/getEmployeesInProject/{userId}")
    public ResponseEntity<List<UserProjectDetailDto>> getEmployeesInProject(@PathVariable int userId) {
        return ResponseEntity.status(200).body(projectServiceImpl.getEmployeesInProject(userId));
    }
}
