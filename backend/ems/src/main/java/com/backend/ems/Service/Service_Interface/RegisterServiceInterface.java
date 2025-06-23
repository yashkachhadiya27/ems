package com.backend.ems.Service.Service_Interface;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.DTO.DepartmentEmployeeCountDTO;
import com.backend.ems.DTO.EditEmployeeDTO;
import com.backend.ems.DTO.EmpDetailsSalaryDTO;
import com.backend.ems.DTO.EmployeesDTO;
import com.backend.ems.DTO.FullNameDto;
import com.backend.ems.DTO.GoogleOAuthUserDto;
import com.backend.ems.DTO.LoginDTO;
import com.backend.ems.DTO.RegisterDTO;
import com.backend.ems.DTO.UpdateProfileDTO;
import com.backend.ems.DTO.UserDetailDTO;
import com.backend.ems.DTO.UserNameDepartment;
import com.backend.ems.Entity.Register;

public interface RegisterServiceInterface {

        public Register registerEmployee(RegisterDTO registerDTO, MultipartFile image) throws IOException;

        public Register employeeById(int id);

        public Register employeeByEmail(String email);

        public LoginDTO loginEmployee(LoginDTO loginDTO);

        public LoginDTO refereshToken(LoginDTO loginDTO);

        public String getImageName();

        public String accessTokenGeneration(String refreshToken);

        public void logout(String refreshToken);

        public void updateProfile(int userId, UpdateProfileDTO employee);

        public void updateEmployeeEmail(Integer otp, String oldEmail, String newEmail);

        public void updateImage(String email, MultipartFile image) throws IOException;

        public CustomResponse isUserExist(String email);

        public UserNameDepartment getUserNameDepartment(String email);

        public Page<EmployeesDTO> getEmployeesDetail(Integer pageNumber, Integer pageSize, String sortBy,
                        String sortOrder);

        public void deleteEmployee(String email);

        public CustomResponse editEmployee(String email, EditEmployeeDTO editEmployeeDTO);

        public EditEmployeeDTO getEditEmployeesDetail(String email);

        public UserDetailDTO getUserDetail(String email);

        public Page<EmployeesDTO> getSearchedEmployee(String keyword, Integer pageNumber, Integer pageSize,
                        String sortBy,
                        String sortOrder);

        public CustomResponse addEmployee(RegisterDTO registerDto, MultipartFile image) throws IOException;

        public int totalEmployee();

        public List<DepartmentEmployeeCountDTO> getTotalEmployeesByDepartment();

        Register employeeByEmailOauth(String email);

        public List<String> getTodaysBirthdays();

        public List<String> getTodaysWorkAnniversarries();

        public List<FullNameDto> getIdAndName();
}
