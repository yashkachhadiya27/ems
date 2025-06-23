package com.backend.ems.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.ems.DTO.AccessTokenResponse;
import com.backend.ems.DTO.CustomResponse;
import com.backend.ems.DTO.DepartmentEmployeeCountDTO;
import com.backend.ems.DTO.EditEmployeeDTO;
import com.backend.ems.DTO.EmployeesDTO;
import com.backend.ems.DTO.FullNameDto;
import com.backend.ems.DTO.LoginDTO;
import com.backend.ems.DTO.RefreshTokenRequest;
import com.backend.ems.DTO.RegisterDTO;
import com.backend.ems.DTO.UpdateProfileDTO;
import com.backend.ems.DTO.UserChatInfoDto;
import com.backend.ems.DTO.UserDetailDTO;
import com.backend.ems.DTO.UserNameDepartment;
import com.backend.ems.Entity.Register;
import com.backend.ems.Service.Service_implementation.RegisterServiceImpl;
import com.backend.ems.Service.Service_implementation.TokenServiceImpl;
import com.backend.ems.Util.JWTUtils;
import com.backend.ems.Util.OAuthHelper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/public")
public class RegisterController {
    private final RegisterServiceImpl registerServiceImpl;
    private final OAuthHelper oAuthHelper;
    private final JWTUtils jwtUtils;
    private final TokenServiceImpl tokenServiceImpl;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/public/oauth2/authorization/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String redirectUrl = "https://accounts.google.com/o/oauth2/auth"
                + "?client_id=113233145582-qeo93okdngmo4dj2p170dm7pvo53pskf.apps.googleusercontent.com"
                + "&redirect_uri=http://localhost:4200/callback"
                + "&response_type=code"
                + "&scope=profile email";
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/public/oauth2/callback")
    public ResponseEntity<?> oauthCallback(@RequestParam("code") String code) throws IOException {
        OidcUser oidcUser = oAuthHelper.processOidcUser(code, "google");
        if (oidcUser == null) {
            return ResponseEntity.status(200).body(new CustomResponse("Please Register ", 111));
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                oidcUser,
                null,
                oidcUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Register user = registerServiceImpl.employeeByEmail(oidcUser.getEmail());
        var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
        var jwt = jwtUtils.generateToken(user);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(2);
        tokenServiceImpl.saveRefreshToken(refreshToken, expirationTime, user);
        LoginDTO login = new LoginDTO();
        login.setEmail(oidcUser.getEmail());
        login.setEmpId(user.getId());
        login.setAccessToken(jwt);
        login.setRole(user.getRole());
        login.setRefreshToken(refreshToken);
        return ResponseEntity.status(200).body(login);
    }

    @PostMapping(path = "/public/register")
    public ResponseEntity<HttpStatus> registerEmployee(@ModelAttribute @Valid RegisterDTO registerdDto,
            @RequestParam("image") @Valid MultipartFile image) throws IOException {

        registerServiceImpl.registerEmployee(registerdDto, image);
        return ResponseEntity.ok(HttpStatus.CREATED);

    }

    @PostMapping(path = "/admin/addEmployee")
    public ResponseEntity<CustomResponse> addEmployee(@ModelAttribute @Valid RegisterDTO registerdDto,
            @RequestParam("image") @Valid MultipartFile image) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED).body(registerServiceImpl.addEmployee(registerdDto, image));

    }

    @GetMapping(path = "/public/isUserExist/{email}")
    public ResponseEntity<CustomResponse> isUserExist(@PathVariable String email) {
        return ResponseEntity.ok(registerServiceImpl.isUserExist(email));
    }

    @GetMapping("/adminEmployee/profileImage")
    public ResponseEntity<Resource> getImage() throws IOException {
        String imageName = registerServiceImpl.getImageName();
        Path path = Paths.get(uploadDir).resolve(imageName);
        Resource resource = new FileSystemResource(path.toFile());

        String contentType = Files.probeContentType(path);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/admin/getUserImage/{imageName}")
    public ResponseEntity<Resource> getUserImage(@PathVariable String imageName) throws IOException {
        Path path = Paths.get(uploadDir).resolve(imageName);
        Resource resource = new FileSystemResource(path.toFile());
        String contentType = Files.probeContentType(path);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/adminEmployee/getUserImage/{imageName}")
    public ResponseEntity<Resource> getUserImageByAll(@PathVariable String imageName) throws IOException {
        Path path = Paths.get(uploadDir).resolve(imageName);
        Resource resource = new FileSystemResource(path.toFile());
        String contentType = Files.probeContentType(path);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @PostMapping(path = "/public/login")
    public ResponseEntity<LoginDTO> loginEmployee(@RequestBody LoginDTO loginDTO) {
        try {
            LoginDTO loginDTO2 = registerServiceImpl.loginEmployee(loginDTO);
            return ResponseEntity.ok(loginDTO2);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @PostMapping("/public/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String result = registerServiceImpl.accessTokenGeneration(refreshToken);
        if (result.equalsIgnoreCase("notValid")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Refresh token has expired and has been removed.");
        }
        return ResponseEntity.ok(new AccessTokenResponse(result));
    }

    @PostMapping("/public/logout")
    public ResponseEntity<?> logoutUser(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        registerServiceImpl.logout(refreshToken);
        return ResponseEntity.status(200).body(new CustomResponse("Logged Out successfully", 200));

    }

    @PatchMapping("/employee/updateProfile/{userId}")
    public ResponseEntity<?> updateEmployee(@PathVariable int userId,
            @ModelAttribute @Valid UpdateProfileDTO employee) {
        registerServiceImpl.updateProfile(userId, employee);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/admin/editEmployee/{email}")
    public ResponseEntity<CustomResponse> editEmployeeDetail(@PathVariable String email,
            @ModelAttribute @Valid EditEmployeeDTO editEmployeeDTO) {
        try {
            return ResponseEntity.ok(registerServiceImpl.editEmployee(email, editEmployeeDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("Something went wrong!", 400));
        }
    }

    @GetMapping("/admin/getEditEmployeeDetail/{email}")
    public ResponseEntity<EditEmployeeDTO> getEditEmployeeDetail(@PathVariable String email) {
        return ResponseEntity.ok(registerServiceImpl.getEditEmployeesDetail(email));
    }

    @PatchMapping("/employee/updateEmployeeEmail/{oldEmail}")
    public ResponseEntity<CustomResponse> updateEmployeeEmail(@PathVariable("oldEmail") String oldEmail,
            @RequestBody Map<String, Object> payload) {
        try {
            String newEmail = (String) payload.get("newEmail");
            Integer otp = (Integer) payload.get("otp");

            registerServiceImpl.updateEmployeeEmail(otp, oldEmail, newEmail);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("success", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("fail", 400));
        }

    }

    @PatchMapping("/employee/updateProfileImage/{email}")
    public ResponseEntity<CustomResponse> updateProfileImage(@PathVariable String email,
            @RequestParam("image") MultipartFile image) {
        try {
            registerServiceImpl.updateImage(email, image);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Success", 200));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomResponse("Fail", 400));

        }
    }

    @GetMapping("/adminEmployee/getUserData/{email}")
    public ResponseEntity<UserDetailDTO> getUserData(@PathVariable String email) {
        try {
            return ResponseEntity.ok(registerServiceImpl.getUserDetail(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserDetailDTO());
        }
    }

    @GetMapping("/employee/search")
    public List<UserChatInfoDto> searchUsers(@RequestParam String query) {

        return registerServiceImpl.searchChatUsersByEmail(query);
    }

    @GetMapping("/admin/getAllEmployee")
    public ResponseEntity<Page<EmployeesDTO>> getAllEmployee(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
        if (keyword.equals("")) {
            return ResponseEntity.ok()
                    .body(registerServiceImpl.getEmployeesDetail(pageNumber, pageSize, sortBy, sortOrder));
        }
        return ResponseEntity.ok()
                .body(registerServiceImpl.getSearchedEmployee(keyword, pageNumber, pageSize, sortBy, sortOrder));

    }

    @GetMapping("/admin/getSearchedEmployees")
    public ResponseEntity<Page<EmployeesDTO>> getSearchedEmployees(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
        if (keyword.equals("")) {
            return ResponseEntity.ok()
                    .body(registerServiceImpl.getEmployeesDetail(pageNumber, pageSize, sortBy, sortOrder));
        }
        return ResponseEntity.ok()
                .body(registerServiceImpl.getSearchedEmployee(keyword, pageNumber, pageSize, sortBy, sortOrder));

    }

    @GetMapping("/employee/employeePoint")
    public ResponseEntity<CustomResponse> getMethodName2() {
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Employee called Hello", 200));
    }

    @GetMapping("/admin/adminPoint")
    public ResponseEntity<CustomResponse> getMethodName1() {
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse("Admin called Hello", 200));
    }

    @GetMapping("/adminEmployee/adminEmployeePoint")
    public String getMethodName3() {
        return "Hello from both one";
    }

    @GetMapping("/adminEmployee/getUserNameDepartment/{email}")
    public ResponseEntity<UserNameDepartment> getUserNameDepartment(@PathVariable String email) {
        return ResponseEntity.ok(registerServiceImpl.getUserNameDepartment(email));
    }

    @DeleteMapping("/admin/deleteEmployee/{email}")
    public ResponseEntity<CustomResponse> deleteEmployee(@PathVariable String email) {
        CustomResponse cr;
        try {
            registerServiceImpl.deleteEmployee(email);
            cr = new CustomResponse("Deleted successfully", 200);
            return ResponseEntity.status(HttpStatus.OK).body(cr);
        } catch (Exception e) {
            cr = new CustomResponse("Something went wrong", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cr);

        }

    }

    @GetMapping("/admin/totalEmployee")
    public ResponseEntity<Integer> totalEmployee() {
        return ResponseEntity.ok(registerServiceImpl.totalEmployee());
    }

    @GetMapping("/admin/department-count")
    public List<DepartmentEmployeeCountDTO> getTotalEmployeesByDepartment() {
        return registerServiceImpl.getTotalEmployeesByDepartment();
    }

    @GetMapping("/adminEmployee/todayBirthday")
    public ResponseEntity<List<String>> getTodaysBirthdays() {
        List<String> birthdays = registerServiceImpl.getTodaysBirthdays();
        return ResponseEntity.ok(birthdays);
    }

    @GetMapping("/adminEmployee/todayWorkAnniversarries")
    public ResponseEntity<List<String>> getTodaysWorkAnniversarries() {
        List<String> work = registerServiceImpl.getTodaysWorkAnniversarries();
        return ResponseEntity.ok(work);
    }

    @GetMapping("/employee/getIdName")
    public ResponseEntity<List<FullNameDto>> getIdAndName() {
        return ResponseEntity.status(HttpStatus.OK).body(registerServiceImpl.getIdAndName());
    }

}
