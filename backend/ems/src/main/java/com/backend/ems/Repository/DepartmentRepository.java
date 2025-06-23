package com.backend.ems.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.ems.Entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("select d from Department d where LOWER(d.departmentName) LIKE %?1%")
    public Page<Department> getSearchedDepartment(String keyword, Pageable pageable);

    public Optional<Department> findByDepartmentName(String deptName);

    @Query("select count(*) from Department d ")
    public int totalDepartment();
}
