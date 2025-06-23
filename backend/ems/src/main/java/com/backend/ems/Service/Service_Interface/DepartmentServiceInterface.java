package com.backend.ems.Service.Service_Interface;

import java.util.List;

import org.springframework.data.domain.Page;

import com.backend.ems.Entity.Department;

public interface DepartmentServiceInterface {
        public boolean addDepartment(Department department);

        public Page<Department> getAllSearchedDepartment(String keyword, Integer pageNumber, Integer pageSize,
                        String sortBy,
                        String sortOrder);

        public Page<Department> getAllDepartment(Integer pageNumber, Integer pageSize,
                        String sortBy,
                        String sortOrder);

        public List<Department> getAllDepartment();

        public void deleteDepartment(int id);

        public void editDepartment(int id, Department department);

        public int totalDepartment();
}
