package com.backend.ems.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.ems.DTO.BirthdayDto;
import com.backend.ems.DTO.DepartmentEmployeeCountDTO;
import com.backend.ems.DTO.EditEmployeeDTO;
import com.backend.ems.DTO.EmpDetailsSalaryDTO;
import com.backend.ems.DTO.EmployeesDTO;
import com.backend.ems.DTO.FullNameDto;
import com.backend.ems.DTO.UserChatInfoDto;
import com.backend.ems.DTO.UserNameDepartment;
import com.backend.ems.DTO.WorkAnnivesariesDto;
import com.backend.ems.Entity.Register;

import jakarta.transaction.Transactional;

@Repository
public interface RegisterRepository extends JpaRepository<Register, Integer> {

        Optional<Register> findByEmail(String email);

        @Query("select r.image from Register r where r.email = :email")
        String getImageByEmail(@Param("email") String email);

        @Modifying
        @Transactional
        @Query("update Register reg set reg.image=:newImageName where reg.email=:email")
        public void updateImageName(String email, String newImageName);

        @Modifying
        @Transactional
        @Query("update Register reg set reg.password=:password where reg.email=:email")
        public void updatePassword(String email, String password);

        @Modifying
        @Transactional
        @Query("update Register reg set reg.email=:newEmail,reg.image=:newImageName where reg.email=:oldEmail")
        public void updateEmployeeEmail(String oldEmail, String newEmail, String newImageName);

        @Query("select new com.backend.ems.DTO.UserNameDepartment(r.image,CONCAT(r.fname,' ',r.lname),r.department) from Register r where r.email=:email")
        public UserNameDepartment getUserNameDepartment(String email);

        @Query("select new com.backend.ems.DTO.UserChatInfoDto(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.department,r.status) from Register r where r.email like %?1%")
        public List<UserChatInfoDto> getSearchedUserChatInfo(String email);

        @Query("select new com.backend.ems.DTO.UserChatInfoDto(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.department,r.status) from Register r ")
        public List<UserChatInfoDto> getAllUserChatInfo(String email);

        @Query("select new com.backend.ems.DTO.EmployeesDTO(r.image,CONCAT(r.fname,' ',r.lname),r.email,r.gender,r.phone,r.department,r.dateOfBirth,r.dateOfJoining) from Register r")
        public Page<EmployeesDTO> getEmployeesDetail(Pageable pageable);

        @Query("select new com.backend.ems.DTO.FullNameDto(r.id,CONCAT(r.fname,' ',r.lname)) from Register r")
        public List<FullNameDto> getIdAndName();

        @Query("select new com.backend.ems.DTO.EditEmployeeDTO(r.fname,r.mname,r.lname,r.email,r.department,r.phone,r.dateOfBirth,r.dateOfJoining) from Register r where r.email=:email")
        public EditEmployeeDTO getEditEmployeesDetail(String email);

        @Query("select new com.backend.ems.DTO.EmpDetailsSalaryDTO(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.email,r.department) from Register r where r.department!='ADMIN'")
        public Page<EmpDetailsSalaryDTO> getAllEmpDetailsSalary(Pageable pageable);

        @Query("select new com.backend.ems.DTO.EmpDetailsSalaryDTO(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.email,r.department) from Register r where LOWER(r.department) LIKE %?1% and r.department!='ADMIN'")
        public Page<EmpDetailsSalaryDTO> getAllSearchedEmpDetailsSalary(String keyword, Pageable pageable);

        @Query("select new com.backend.ems.DTO.EmpDetailsSalaryDTO(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.email,r.department,s) from Register r Join Salary s on r.id = s.register_id where r.department!='ADMIN' and MONTH(s.month)=:month and YEAR(s.month)=:year ")
        public Page<EmpDetailsSalaryDTO> getAllPaySalary(@Param("month") int month, @Param("year") int year,
                        Pageable pageable);

        @Query("select new com.backend.ems.DTO.EmpDetailsSalaryDTO(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.email,r.department,s) from Register r join Salary s on r.id=s.register_id where LOWER(r.department) LIKE %:keyword% and r.department!='ADMIN' and MONTH(s.month)=:month and YEAR(s.month)=:year")
        public Page<EmpDetailsSalaryDTO> getAllSearchedPaySalary(@Param("keyword") String keyword,
                        @Param("month") int month, @Param("year") int year,
                        Pageable pageable);

        @Query("select new com.backend.ems.DTO.EmpDetailsSalaryDTO(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.email,r.department,s) from Register r Join Salary s on r.id = s.register_id where r.id=:userId ORDER BY s.month DESC")
        public Page<EmpDetailsSalaryDTO> getEmpAllSalary(@Param("userId") int userId,
                        Pageable pageable);

        @Query("select new com.backend.ems.DTO.EmpDetailsSalaryDTO(r.id,r.image,CONCAT(r.fname,' ',r.lname),r.email,r.department,s) from Register r join Salary s on r.id=s.register_id where r.id=:userId and TO_CHAR(s.month, 'YYYY-MM') = :keyword")
        public Page<EmpDetailsSalaryDTO> getEmpAllSearchedSalary(@Param("keyword") String keyword,
                        @Param("userId") int userId,
                        Pageable pageable);

        @Modifying
        @Transactional
        public void deleteByEmail(String email);

        @Query("select new com.backend.ems.DTO.EmployeesDTO(r.image,CONCAT(r.fname,' ',r.lname),r.email,r.gender,r.phone,r.department,r.dateOfBirth,r.dateOfJoining) from Register r where LOWER(concat(r.fname,' ',r.lname,' ',r.phone,' ',r.department,' ',r.email,' ',r.gender)) LIKE %?1%")
        public Page<EmployeesDTO> getSearchedEmployee(String keyword, Pageable pageable);

        @Query("select r.id from Register r where r.email=:email")
        public int getIdFromEmail(String email);

        @Query("select r.department from Register r where r.id=:id")
        public String getDepartmentFromId(int id);

        @Query("select count(*) from Register r ")
        public int totalEmployee();

        @Query("SELECT new com.backend.ems.DTO.DepartmentEmployeeCountDTO(r.department, COUNT(r)) FROM Register r GROUP BY r.department")
        public List<DepartmentEmployeeCountDTO> getTotalEmployeesByDepartment();

        @Query("SELECT CONCAT(r.fname, ' ', r.lname) FROM Register r WHERE EXTRACT(MONTH FROM r.dateOfBirth) = :month AND EXTRACT(DAY FROM r.dateOfBirth) = :day")
        List<String> findByDateOfBirthMonthAndDay(@Param("month") int month, @Param("day") int day);

        @Query("select new com.backend.ems.DTO.BirthdayDto(CONCAT(r.fname,' ',r.lname),r.email) from Register r WHERE EXTRACT(MONTH FROM r.dateOfBirth) = :month AND EXTRACT(DAY FROM r.dateOfBirth) = :day")
        List<BirthdayDto> findByDateOfBirthMonthAndDayForEmail(@Param("month") int month, @Param("day") int day);

        @Query("SELECT CONCAT(r.fname, ' ', r.lname) FROM Register r WHERE EXTRACT(MONTH FROM r.dateOfJoining) = :month AND EXTRACT(DAY FROM r.dateOfJoining) = :day")
        List<String> findByDateOfJoiningMonthAndDay(@Param("month") int month,
                        @Param("day") int day);

        List<Register> findByEmailContaining(String email);

}
