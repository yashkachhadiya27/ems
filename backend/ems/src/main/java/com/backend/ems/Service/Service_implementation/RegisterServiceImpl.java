package com.backend.ems.Service.Service_implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
import com.backend.ems.DTO.UserChatInfoDto;
import com.backend.ems.DTO.UserDetailDTO;
import com.backend.ems.DTO.UserNameDepartment;
import com.backend.ems.Entity.Address;
import com.backend.ems.Entity.Experience;
import com.backend.ems.Entity.Register;
import com.backend.ems.Exception.CustomJWTException;
import com.backend.ems.Exception.EmployeeExistsException;
import com.backend.ems.Exception.EmployeeNotFoundException;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.UserMapper;
import com.backend.ems.Service.Service_Interface.RegisterServiceInterface;
import com.backend.ems.Util.JWTUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterServiceInterface {
    private final RegisterRepository registerRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final TokenServiceImpl tokenServiceImpl;
    private final OtpServiceImpl otpServiceImpl;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public Register registerEmployee(RegisterDTO registerDto, MultipartFile image) throws IOException {
        Optional<Register> isEmployeeAlreadyExist = registerRepository.findByEmail(registerDto.getEmail());
        if (isEmployeeAlreadyExist.isPresent()) {
            throw new EmployeeExistsException("Employee exists with id: " + registerDto.getEmail());
        }
        return otpServiceImpl.verifyOtpAndRegister(registerDto, image);

    }

    @Override
    public CustomResponse addEmployee(RegisterDTO registerDto, MultipartFile image) throws IOException {
        Optional<Register> isEmployeeAlreadyExist = registerRepository.findByEmail(registerDto.getEmail());
        if (isEmployeeAlreadyExist.isPresent()) {
            return new CustomResponse("Already Exists", 208);
        }
        otpServiceImpl.addEmployeeDetail(registerDto, image);
        return new CustomResponse("Success", 201);

    }

    @Override
    public Register employeeById(int id) {
        return registerRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found."));
    }

    public int getIdFromEmail(String email) {
        return registerRepository.getIdFromEmail(email);
    }

    @Override
    public Register employeeByEmail(String email) {
        return registerRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with email " + email + " not found."));
    }

    @Override
    public Register employeeByEmailOauth(String email) {
        return registerRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserDetailDTO getUserDetail(String email) {
        Register reg = employeeByEmail(email);
        reg.setImage("http://localhost:9090/admin/getUserImage/" + reg.getImage());
        // UserDetailDTO userDetailDTO = new UserDetailDTO();

        // userDetailDTO.setFname(reg.getFname());
        // userDetailDTO.setMname(reg.getMname());
        // userDetailDTO.setLname(reg.getLname());
        // userDetailDTO.setEmail(reg.getEmail());
        // userDetailDTO.setGender(reg.getGender());
        // userDetailDTO.setImage("http://localhost:9090/admin/getUserImage/" +
        // reg.getImage());
        // userDetailDTO.setDepartment(reg.getDepartment());
        // userDetailDTO.setDateOfJoining(reg.getDateOfJoining());
        // userDetailDTO.setDateOfBirth(reg.getDateOfBirth());
        // userDetailDTO.setPhone(reg.getPhone());
        // userDetailDTO.setSkills(reg.getSkills()); // Assuming it's already a String[]
        // userDetailDTO.setAddress(reg.getAddress()); // Assuming Address is directly
        // compatible
        // userDetailDTO.setExperience(reg.getExperience());
        return UserMapper.INSTANCE.toUserDetailDTO(reg);
        // return userDetailDTO;
    }

    @Override
    public LoginDTO loginEmployee(LoginDTO loginDTO) {
        LoginDTO login = new LoginDTO();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),
                            loginDTO.getPassword()));
            var user = registerRepository.findByEmail(loginDTO.getEmail()).orElseThrow(
                    () -> new EmployeeNotFoundException("Employee with email " + loginDTO.getEmail() + " not found."));
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(2); // Set expiration date to 14 days from
                                                                               // now
            tokenServiceImpl.saveRefreshToken(refreshToken, expirationTime, user);
            login.setEmpId(user.getId());
            login.setAccessToken(jwt);
            login.setRole(user.getRole());
            login.setRefreshToken(refreshToken);
            return login;
        } catch (Exception e) {
            throw new CustomJWTException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public LoginDTO refereshToken(LoginDTO loginDTO) {
        LoginDTO login = new LoginDTO();
        try {
            String ourEmail = jwtUtils.extractUsername(loginDTO.getAccessToken());
            var user = registerRepository.findByEmail(ourEmail).orElseThrow(
                    () -> new EmployeeNotFoundException("Employee with email " + loginDTO.getEmail() + " not found."));
            if (jwtUtils.isTokenValid(loginDTO.getAccessToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
                login.setAccessToken(jwt);
                login.setRole(user.getRole());
                login.setRefreshToken(refreshToken);
                return login;

            } else {
                throw new CustomJWTException("Invalid JWT token.");
            }
        } catch (Exception e) {
            throw new CustomJWTException("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    public String getImageName() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return registerRepository.getImageByEmail(userEmail);
    }

    @Override
    public String accessTokenGeneration(String refreshToken) {
        // System.out.println(refreshToken);
        if (tokenServiceImpl.validateAndRemoveExpiredRefreshToken(refreshToken)) {
            String username = jwtUtils.extractUsernameRefreshToken(refreshToken);
            return jwtUtils.generateToken(employeeByEmail(username));
        } else {
            return "notValid";
        }
    }

    @Override
    public void logout(String refreshToken) {
        tokenServiceImpl.removeRefreshToken(refreshToken);
    }

    @Override
    public void updateProfile(int userId, UpdateProfileDTO employee) {
        Register register = employeeById(userId);
        if (employee.getFname() != null) {
            register.setFname(employee.getFname());
        }
        if (employee.getLname() != null) {
            register.setLname(employee.getLname());
        }
        if (employee.getMname() != null) {
            register.setMname(employee.getMname());
        }
        if (employee.getGender() != null) {
            register.setGender(employee.getGender());
        }
        if (employee.getDepartment() != null) {
            register.setDepartment(employee.getDepartment());
        }
        if (employee.getDateOfBirth() != null) {
            register.setDateOfBirth(employee.getDateOfBirth());
        }
        if (employee.getDateOfJoining() != null) {
            register.setDateOfJoining(employee.getDateOfJoining());
        }
        if (employee.getPhone() != null) {
            register.setPhone(employee.getPhone());
        }
        if (employee.getSkills() != null) {
            register.setSkills(employee.getSkills());
        }

        // Handle Address
        Address address = register.getAddress();
        if (address == null) {
            address = new Address();
        }
        if (employee.getStreet() != null) {
            address.setStreet(employee.getStreet());
        }
        if (employee.getPostalcode() != null) {
            address.setPostalcode(employee.getPostalcode());
        }
        if (employee.getDistrict() != null) {
            address.setDistrict(employee.getDistrict());
        }
        if (employee.getState() != null) {
            address.setState(employee.getState());
        }
        if (employee.getCity() != null) {
            address.setCity(employee.getCity());
        }
        if (employee.getCountry() != null) {
            address.setCountry(employee.getCountry());
        }

        register.setAddress(address);

        registerRepository.save(register);

    }

    @Override
    public void updateEmployeeEmail(Integer otp, String oldEmail, String newEmail) {
        // otpServiceImpl.generateAndSendOtp(newEmail); this we will do by calling
        // send-otp end point available in OtpController
        // updateEmployee will be called when updateEmployeeEmail end point called
        otpServiceImpl.verifyOtpAndUpdateEmail(otp, oldEmail, newEmail);
    }

    @Override
    public void updateImage(String email, MultipartFile image) throws IOException {
        Register register = employeeByEmail(email);
        if (register.getImage() != null) {
            deleteImage(register.getImage());
        }
        saveImage(email, image);
    }

    public void deleteImage(String imageName) {
        Path path = Paths.get(uploadDir).resolve(imageName);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete image file: " + e.getMessage());
        }
    }

    public void saveImage(String email, MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.lastIndexOf('.') > 0) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
        }
        String imageName = email + "." + extension;
        Path path = Paths.get(uploadDir, imageName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());

        Register register = employeeByEmail(email);
        register.setImage(imageName);
        registerRepository.updateImageName(email, imageName);
    }

    @Override
    public CustomResponse isUserExist(String email) {
        CustomResponse response;
        Optional<Register> isEmployeeAlreadyExist = registerRepository.findByEmail(email);
        if (isEmployeeAlreadyExist.isPresent()) {
            response = new CustomResponse("Exist", 208);
        } else {
            response = new CustomResponse("Not Exist", 404);
        }
        return response;
    }

    @Override
    public UserNameDepartment getUserNameDepartment(String email) {
        return registerRepository.getUserNameDepartment(email);
    }

    @Override
    public Page<EmployeesDTO> getEmployeesDetail(Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<EmployeesDTO> employees = registerRepository.getEmployeesDetail(pageable);
        employees.forEach((reg) -> {
            reg.setImage("http://localhost:9090/admin/getUserImage/" + reg.getImage());
        });
        return employees;
    }

    @Override
    public void deleteEmployee(String email) {
        registerRepository.deleteByEmail(email);
    }

    @Override
    public CustomResponse editEmployee(String email, EditEmployeeDTO editEmp) {

        Register reg = employeeByEmail(email);
        reg.setFname(editEmp.getFname());
        reg.setMname(editEmp.getMname());
        reg.setLname(editEmp.getLname());
        reg.setEmail(editEmp.getEmail());
        reg.setDepartment(editEmp.getDepartment());
        reg.setPhone(editEmp.getPhone());
        reg.setDateOfBirth(editEmp.getDateOfBirth());
        reg.setDateOfJoining(editEmp.getDateOfJoining());
        registerRepository.save(reg);
        return new CustomResponse("Success", 200);

    }

    @Override
    public EditEmployeeDTO getEditEmployeesDetail(String email) {
        return registerRepository.getEditEmployeesDetail(email);
    }

    @Override
    public Page<EmployeesDTO> getSearchedEmployee(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<EmployeesDTO> employees = registerRepository.getSearchedEmployee(keyword.toLowerCase(), pageable);
        employees.forEach((reg) -> {
            reg.setImage("http://localhost:9090/admin/getUserImage/" + reg.getImage());
        });
        return employees;
    }

    @Override
    public int totalEmployee() {
        return registerRepository.totalEmployee();
    }

    @Override
    public List<DepartmentEmployeeCountDTO> getTotalEmployeesByDepartment() {
        return registerRepository.getTotalEmployeesByDepartment();
    }

    @Override
    public List<String> getTodaysBirthdays() {
        LocalDate today = LocalDate.now();
        return registerRepository.findByDateOfBirthMonthAndDay(today.getMonthValue(), today.getDayOfMonth());
    }

    @Override
    public List<String> getTodaysWorkAnniversarries() {
        LocalDate today = LocalDate.now();
        return registerRepository.findByDateOfJoiningMonthAndDay(today.getMonthValue(), today.getDayOfMonth());
    }

    @Override
    public List<FullNameDto> getIdAndName() {
        return registerRepository.getIdAndName();
    }

    public List<UserChatInfoDto> searchChatUsersByEmail(String email) {
        List<UserChatInfoDto> users = registerRepository.getSearchedUserChatInfo(email);
        users.forEach((u) -> {
            u.setImage("http://localhost:9090/adminEmployee/getUserImage/" + u.getImage());
        });
        return users;
    }

    public List<UserChatInfoDto> allChatUsersByEmail(String email) {
        List<UserChatInfoDto> users = registerRepository.getAllUserChatInfo(email);
        return users;
    }

}
