package com.backend.ems.Service.Service_Interface;

import java.util.Date;

import org.springframework.data.domain.Page;

import com.backend.ems.DTO.AddSalaryDTO;
import com.backend.ems.DTO.EmpDetailsSalaryDTO;

public interface SalaryServiceInterface {
    public void addSalary(int userId, AddSalaryDTO addSalaryDTO);

    public Page<EmpDetailsSalaryDTO> getAllPaySalary(int month, int year, Integer pageNumber, Integer pageSize);

    public Page<EmpDetailsSalaryDTO> getAllSearchedPaySalary(String keyword, int month, int year,
            Integer pageNumber,
            Integer pageSize);

    public Page<EmpDetailsSalaryDTO> getEmpDetailsSalary(Integer pageNumber, Integer pageSize);

    public Page<EmpDetailsSalaryDTO> getAllSearchedEmpDetailsSalary(String keyword, Integer pageNumber,
            Integer pageSize);

    public Page<EmpDetailsSalaryDTO> getEmpAllSalary(int userId, Integer pageNumber, Integer pageSize);

    public Page<EmpDetailsSalaryDTO> getEmpAllSearchedSalary(String date, int userId, Integer pageNumber,
            Integer pageSize);

    public boolean paySalary(int salaryId);
}
