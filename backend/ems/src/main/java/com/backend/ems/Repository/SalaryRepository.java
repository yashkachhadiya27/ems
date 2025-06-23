package com.backend.ems.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.ems.Entity.Salary;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Integer> {

}
