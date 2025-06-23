package com.backend.ems.Service.Service_implementation;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.ems.DTO.AddSalaryDTO;
import com.backend.ems.DTO.EmpDetailsSalaryDTO;
import com.backend.ems.Entity.Salary;
import com.backend.ems.Repository.LeaveRequestRepository;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Repository.SalaryRepository;
import com.backend.ems.Service.Service_Interface.SalaryServiceInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryServiceInterface {

    private final SalaryRepository salaryRepository;
    private final RegisterRepository registerRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public void addSalary(int userId, AddSalaryDTO addSalaryDTO) {
        Salary sal = new Salary();
        sal.setBasicPay(addSalaryDTO.getBasic());
        sal.setDearnessAllowance(addSalaryDTO.getAllowance());
        sal.setRegister_id(userId);
        sal.setMonth(new Date());
        sal.setPaid(false);
        String department = registerRepository.getDepartmentFromId(userId);
        applyDepartmentBasedValues(department, addSalaryDTO, sal);
        salaryRepository.save(sal);
    }

    private void applyDepartmentBasedValues(String department, AddSalaryDTO addSalaryDTO, Salary salary) {
        double basicPay = addSalaryDTO.getBasic();

        switch (department.toUpperCase()) {
            case "HR":
                salary.setHouseRentAllowance(basicPay * 0.10);
                salary.setMedicalAllowance(basicPay * 0.05);
                salary.setCorporateAttireAllowance(1000);
                salary.setRegularBonus(basicPay * 0.10);
                salary.setTaxDeducted(2000);
                salary.setProfessionalTax(1500);
                salary.setProvidentFund(3000);
                break;

            case "Accounts":
                salary.setHouseRentAllowance(basicPay * 0.12);
                salary.setMedicalAllowance(basicPay * 0.04);
                salary.setCorporateAttireAllowance(1200);
                salary.setRegularBonus(basicPay * 0.08);
                salary.setTaxDeducted(2500);
                salary.setProfessionalTax(1700);
                salary.setProvidentFund(3500);
                break;

            case "Sales":
                salary.setHouseRentAllowance(basicPay * 0.15);
                salary.setMedicalAllowance(basicPay * 0.06);
                salary.setCorporateAttireAllowance(1100);
                salary.setRegularBonus(basicPay * 0.15);
                salary.setTaxDeducted(2200);
                salary.setProfessionalTax(1600);
                salary.setProvidentFund(3200);
                break;

            case "Marketing":
                salary.setHouseRentAllowance(basicPay * 0.13);
                salary.setMedicalAllowance(basicPay * 0.05);
                salary.setCorporateAttireAllowance(1300);
                salary.setRegularBonus(basicPay * 0.12);
                salary.setTaxDeducted(2100);
                salary.setProfessionalTax(1550);
                salary.setProvidentFund(3100);
                break;

            case "DevOps":
                salary.setHouseRentAllowance(basicPay * 0.14);
                salary.setMedicalAllowance(basicPay * 0.06);
                salary.setCorporateAttireAllowance(1400);
                salary.setRegularBonus(basicPay * 0.10);
                salary.setTaxDeducted(2700);
                salary.setProfessionalTax(1800);
                salary.setProvidentFund(3700);
                break;

            case "Programmer Analyst":
                salary.setHouseRentAllowance(basicPay * 0.12);
                salary.setMedicalAllowance(basicPay * 0.05);
                salary.setCorporateAttireAllowance(1200);
                salary.setRegularBonus(basicPay * 0.12);
                salary.setTaxDeducted(2300);
                salary.setProfessionalTax(1650);
                salary.setProvidentFund(3400);
                break;

            default:
                salary.setHouseRentAllowance(basicPay * 0.10);
                salary.setMedicalAllowance(basicPay * 0.05);
                salary.setCorporateAttireAllowance(1000);
                salary.setRegularBonus(basicPay * 0.10);
                salary.setTaxDeducted(2000);
                salary.setProfessionalTax(1500);
                salary.setProvidentFund(3000);
                break;
        }
    }

    @Override
    public Page<EmpDetailsSalaryDTO> getEmpDetailsSalary(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<EmpDetailsSalaryDTO> employees = registerRepository.getAllEmpDetailsSalary(pageable);
        employees.forEach((reg) -> {
            reg.setImage("http://localhost:9090/admin/getUserImage/" + reg.getImage());
        });
        return employees;
    }

    @Override
    public Page<EmpDetailsSalaryDTO> getAllSearchedEmpDetailsSalary(String keyword, Integer pageNumber,
            Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<EmpDetailsSalaryDTO> employees = registerRepository.getAllSearchedEmpDetailsSalary(keyword.toLowerCase(),
                pageable);
        employees.forEach((reg) -> {
            reg.setImage("http://localhost:9090/admin/getUserImage/" + reg.getImage());
        });
        return employees;
    }

    @Override
    public Page<EmpDetailsSalaryDTO> getAllPaySalary(int month, int year, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<EmpDetailsSalaryDTO> employees = registerRepository.getAllPaySalary(month, year, pageable);
        employees.forEach((reg) -> {
            reg.setImage("http://localhost:9090/admin/getUserImage/" + reg.getImage());
        });
        return employees;
    }

    @Override
    public Page<EmpDetailsSalaryDTO> getAllSearchedPaySalary(String keyword, int month, int year,
            Integer pageNumber,
            Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<EmpDetailsSalaryDTO> employees = registerRepository.getAllSearchedPaySalary(keyword.toLowerCase(), month,
                year,
                pageable);
        employees.forEach((reg) -> {
            reg.setImage("http://localhost:9090/admin/getUserImage/" + reg.getImage());
        });
        return employees;
    }

    @Override
    @Transactional
    public boolean paySalary(int salaryId) {
        Optional<Salary> salOpt = salaryRepository.findById(salaryId);
        if (salOpt.isPresent()) {
            Salary salary = salOpt.get();

            int userId = salary.getRegister_id();
            Date salaryMonth = salary.getMonth();

            if (salaryMonth == null) {
                throw new IllegalArgumentException("Salary month cannot be null.");
            }

            LocalDate localSalaryDate = LocalDate.parse(salaryMonth.toString());
            int month = localSalaryDate.getMonthValue();
            int year = localSalaryDate.getYear();
            int lopDays = leaveRequestRepository.getLOPDaysForMonth(userId, month, year);

            double totalMonthlySalary = salary.getBasicPay();
            if (totalMonthlySalary <= 0) {
                throw new IllegalArgumentException("Total monthly salary must be positive.");
            }

            int totalWorkingDays = getTotalWorkingDaysInMonth(salaryMonth);
            if (totalWorkingDays <= 0) {
                throw new IllegalArgumentException("Total working days must be positive.");
            }

            double dailySalary = totalMonthlySalary / totalWorkingDays;

            double lopDeduction = lopDays * dailySalary;

            double newSalary = totalMonthlySalary - lopDeduction;
            if (newSalary < 0) {
                throw new IllegalArgumentException("Salary cannot be negative after LOP deduction.");
            }
            salary.setBasicPay(newSalary);
            salary.setPaid(true);
            salary.setPaidOn(new Date());
            salaryRepository.save(salary);
            return true;
        }
        return false;
    }

    public int getTotalWorkingDaysInMonth(Date salaryMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(salaryMonth);
        int totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return totalDays - getTotalWeekendsInMonth(salaryMonth);
    }

    public int getTotalWeekendsInMonth(Date salaryMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(salaryMonth);

        int totalWeekends = 0;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                totalWeekends++;
            }
        }

        return totalWeekends;
    }

    @Override
    public Page<EmpDetailsSalaryDTO> getEmpAllSalary(int userId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<EmpDetailsSalaryDTO> employees = registerRepository.getEmpAllSalary(userId, pageable);
        return employees;
    }

    @Override
    public Page<EmpDetailsSalaryDTO> getEmpAllSearchedSalary(String date, int userId, Integer pageNumber,
            Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<EmpDetailsSalaryDTO> employees = registerRepository.getEmpAllSearchedSalary(date, userId,
                pageable);

        return employees;
    }
}
