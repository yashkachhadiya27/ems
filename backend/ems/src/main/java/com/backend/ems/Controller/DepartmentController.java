package com.backend.ems.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.Entity.Department;
import com.backend.ems.Service.Service_implementation.DepartmentServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/admin")
public class DepartmentController {
    private final DepartmentServiceImpl departmentServiceImpl;

    @PostMapping("/admin/addDepartment")
    public ResponseEntity<CustomResponse> addDepartment(@RequestBody Department department) {

        try {
            boolean status = departmentServiceImpl.addDepartment(department);
            if (status) {
                return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));
            }
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new CustomResponse("Fail", 208));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("Fail", 400));

        }

    }

    @GetMapping("/admin/getAllDepartment")
    public ResponseEntity<Page<Department>> getAllDepartment(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
        if (keyword.equals("")) {
            return ResponseEntity.ok()
                    .body(departmentServiceImpl.getAllDepartment(pageNumber, pageSize, sortBy, sortOrder));
        }
        return ResponseEntity.ok()
                .body(departmentServiceImpl.getAllSearchedDepartment(keyword, pageNumber, pageSize, sortBy, sortOrder));

    }

    @GetMapping("/public/getDepartments")
    public ResponseEntity<List<Department>> getAllDepartment() {
        return ResponseEntity.ok().body(departmentServiceImpl.getAllDepartment());
    }

    @DeleteMapping("/admin/deleteDepartment/{id}")
    public ResponseEntity<CustomResponse> deleteDepartment(@PathVariable int id) {
        try {
            departmentServiceImpl.deleteDepartment(id);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("Fail", 400));
        }
    }

    @PatchMapping("/admin/editDepartment/{id}")
    public ResponseEntity<CustomResponse> editDepartment(@PathVariable int id, @RequestBody Department department) {
        try {
            Department dept = new Department();
            dept.setDepartmentName(department.getDepartmentName());
            departmentServiceImpl.editDepartment(id, dept);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("Fail", 400));
        }
    }

    @GetMapping("/admin/totalDepartment")
    public ResponseEntity<Integer> totalDepartment() {
        return ResponseEntity.ok(departmentServiceImpl.totalDepartment());
    }
}
