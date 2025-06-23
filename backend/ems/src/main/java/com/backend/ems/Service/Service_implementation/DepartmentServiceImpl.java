package com.backend.ems.Service.Service_implementation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.backend.ems.Entity.Department;
import com.backend.ems.Repository.DepartmentRepository;
import com.backend.ems.Service.Service_Interface.DepartmentServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentServiceInterface {
    private final DepartmentRepository departmentRepository;

    @Override
    public boolean addDepartment(Department department) {
        Department d = departmentRepository.findByDepartmentName(department.getDepartmentName()).orElse(null);
        boolean status;
        if (d == null) {
            status = true;
            departmentRepository.save(department);
            return status;
        }
        status = false;
        return status;
    }

    @Override
    public Page<Department> getAllSearchedDepartment(String keyword, Integer pageNumber, Integer pageSize,
            String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        return departmentRepository.getSearchedDepartment(keyword, pageable);
    }

    @Override
    public Page<Department> getAllDepartment(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        return departmentRepository.findAll(pageable);
    }

    @Override
    public void deleteDepartment(int id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public void editDepartment(int id, Department department) {
        Department depart = departmentRepository.findById(id).orElse(null);
        if (depart == null) {
            throw new RuntimeException("Error");
        }
        depart.setDepartmentName(department.getDepartmentName());
        departmentRepository.save(depart);
    }

    @Override
    public List<Department> getAllDepartment() {
        return departmentRepository.findAll();
    }

    @Override
    public int totalDepartment() {
        return departmentRepository.totalDepartment();
    }
}
