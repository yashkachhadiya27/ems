package com.backend.ems.Service.Service_implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.ems.DTO.RegisterDTO;
import com.backend.ems.Entity.Address;
import com.backend.ems.Entity.Experience;
import com.backend.ems.Entity.Otp;
import com.backend.ems.Entity.Register;
import com.backend.ems.Exception.OtpExpiredException;
import com.backend.ems.Exception.OtpNotFoundException;
import com.backend.ems.Repository.OtpRepository;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.Service_Interface.OtpServiceInterface;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpServiceInterface {
    private final OtpRepository otpRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final RegisterRepository registerRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public void generateAndSendOtp(String email) {
        Optional<Otp> otpOptional = otpRepository.findByEmail(email);
        Otp otp;

        if (otpOptional.isPresent()) {
            otp = otpOptional.get();
        } else {
            otp = new Otp();
            otp.setEmail(email);
        }
        int otpCode = otpGenerator();
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(LocalDateTime.now().plusMinutes(2));

        otpRepository.save(otp);
        // String body = "This is the OTP from Employee Management System:" + otpCode;
        String body = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>OTP Verification</title>" +
                "<style>" +
                "body {margin: 0; padding: 0; width: 100%; font-family: Arial, sans-serif; background-color: #f4f4f4;}"
                +
                ".container {max-width: 600px; width: 100%; margin: 0 auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}"
                +
                ".header {text-align: center; padding-bottom: 20px;}" +
                ".header h1 {margin: 0; color: #007BFF; font-size: 24px;}" +
                ".content {font-size: 16px; line-height: 1.5; color: #333; padding-left: 0; padding-right: 0;}" +
                ".otp {display: block; max-width: 250px; margin: 20px auto; padding: 15px; text-align: center; background-color: #007BFF; color: #fff; font-size: 24px; font-weight: bold; border-radius: 5px;}"
                +
                ".footer {margin-top: 20px; font-size: 14px; text-align: center; color: #666; padding-left: 0; padding-right: 0;}"
                +
                ".footer a {color: #007BFF; text-decoration: none;}" +
                "@media (max-width: 600px) { .container { padding: 10px; } .header h1 { font-size: 20px; } .otp { font-size: 20px; padding: 10px; }}"
                +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div style='max-width: 100%; overflow-x: hidden;'>" +
                "<div class='container'>" +
                "<div class='header'><h1>OTP Verification</h1></div>" +
                "<div class='content'>" +
                "<p>Hello Sir/Ma'am,</p>" +
                "<p>Thank you for using our service. To complete your verification, please use the following One-Time Password (OTP):</p>"
                +
                "<div class='otp'>" + otpCode + "</div>" +
                "<p>This OTP is valid for the next 2 minutes. If you did not request this code, please disregard this email or contact our support team.</p>"
                +
                "</div>" +
                "<div class='footer'>" +
                "<p>Need help? <a href='mailto:support@example.com'>Contact our support team</a>.</p>" +
                "<p>&copy; " + java.time.Year.now().getValue() + " Argusoft. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailServiceImpl.sendEmail(null, email, null, null, "OTP From EMS.", body);
    }

    @Override
    @Transactional
    public Register verifyOtpAndRegister(RegisterDTO registerDTO, MultipartFile image) throws IOException {
        Otp otp = otpRepository
                .findByOtpCodeAndEmail(Integer.parseInt(registerDTO.getOtpCode()), registerDTO.getEmail())
                .orElseThrow(() -> new OtpNotFoundException("Invalid OTP"));

        if (otp == null || otp.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired");
        }
        Register r = addEmployeeDetail(registerDTO, image);
        otpRepository.delete(otp);
        return r;
    }

    @Transactional
    public Register addEmployeeDetail(RegisterDTO registerDTO, MultipartFile image) throws IOException {
        Register register = new Register();
        register.setFname(registerDTO.getFname());
        register.setLname(registerDTO.getLname());
        register.setMname(registerDTO.getMname());
        register.setEmail(registerDTO.getEmail().toLowerCase());
        register.setGender(registerDTO.getGender());
        register.setDepartment(registerDTO.getDepartment());
        register.setDateOfJoining(registerDTO.getDateOfJoining());
        register.setDateOfBirth(registerDTO.getDateOfBirth());
        if (registerDTO.getPassword() != null) {
            register.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        } else {

            String generatedPassword = generatePassword(8);
            register.setPassword(passwordEncoder.encode(generatedPassword));
            emailServiceImpl.sendEmail(null, registerDTO.getEmail(), null, null, "Login credentials ",
                    composeEmailBody(registerDTO.getEmail(), registerDTO.getFname(), generatedPassword));
        }
        register.setSkills(registerDTO.getSkills());
        register.setPhone(registerDTO.getPhone());
        register.setRole("USER");

        Address adr = new Address(registerDTO.getStreet(), registerDTO.getPostalcode(),
                registerDTO.getDistrict(), registerDTO.getCity(), registerDTO.getCountry(), registerDTO.getState());
        register.setAddress(adr);

        if (registerDTO.getExperience() != null) {
            List<Experience> arrayOfObjects = objectMapper.readValue(registerDTO.getExperience(),
                    new TypeReference<List<Experience>>() {
                    });
            register.setExperience(arrayOfObjects);
        }
        String originalFileName = image.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.lastIndexOf('.') > 0) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
        }
        String imageName = registerDTO.getEmail() + "." + extension;
        Path path = Paths.get(uploadDir, imageName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        register.setImage(imageName);
        registerRepository.save(register);

        return register;
    }

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_+=<>?";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIALS;
    private static SecureRandom random = new SecureRandom();

    public String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIALS.charAt(random.nextInt(SPECIALS.length())));

        for (int i = 4; i < length; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        return password.toString();
    }

    public String composeEmailBody(String email, String firstName, String generatedPassword) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<p style='font-size: 16px;'>Dear <strong>" + firstName + "</strong>,</p>" +

                "<p>Welcome to our platform! An account has been created for you.</p>" +

                "<p>Here are your login credentials:</p>" +
                "<table style='border-collapse: collapse; width: 100%;'>" +
                "  <tr>" +
                "    <td style='padding: 8px; border-bottom: 1px solid #ddd;'>Username:</td>" +
                "    <td style='padding: 8px; border-bottom: 1px solid #ddd;'>" + email + "</td>" +
                "  </tr>" +
                "  <tr>" +
                "    <td style='padding: 8px; border-bottom: 1px solid #ddd;'>Password:</td>" +
                "    <td style='padding: 8px; border-bottom: 1px solid #ddd;'><strong>" + generatedPassword
                + "</strong></td>" +
                "  </tr>" +
                "</table>" +

                "<p style='background-color: #f9f9f9; padding: 12px; border-left: 4px solid #4CAF50;'>" +
                "This is an auto-generated password, and we highly recommend that you change it after logging in for security purposes."
                +
                "</p>" +

                "<p>To change your password, please follow these steps:</p>" +
                "<ol style='padding-left: 20px;'>" +
                "  <li>Login with given credentials.</li>" +
                "  <li>Go to Update Profile page.</li>" +
                "  <li>In the top right side of the form there is 3 dots click on that and then change password.</li>" +
                "  <li>Create new Password.</li>" +
                "</ol>" +

                "<p>Click below link and follow above instructions to generate new password:</p>" +
                "<p><a href='http://localhost:4200/login' style='color: #4CAF50; text-decoration: none; font-weight: bold;'>Click here to Change Password</a></p>"
                +

                "<p>If you have any issues, feel free to contact our support team.</p>" +

                "<p style='font-size: 16px;'>Best regards,</p>" +
                "<p><strong>The Argusoft Team</strong></p>" +
                "</body>" +
                "</html>";
    }

    @Override
    public void resendOtp(String email) {
        generateAndSendOtp(email);
    }

    @Override
    @Transactional
    public void verifyOtpAndUpdateEmail(Integer otp, String oldEmail, String newEmail) {
        Otp otp1 = otpRepository
                .findByOtpCodeAndEmail(otp, newEmail)
                .orElseThrow(() -> new OtpNotFoundException("Invalid OTP"));

        if (otp1 == null || otp1.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired");
        }
        String newImageName = renameImage(oldEmail, newEmail);

        registerRepository.updateEmployeeEmail(oldEmail, newEmail, newImageName);
        otpRepository.delete(otp1);

    }

    public String renameImage(String oldEmail, String newEmail) {
        String oldImageName = registerRepository.getImageByEmail(oldEmail);
        Path oldImagePath = Paths.get(uploadDir, oldImageName);

        String extension = "";
        if (oldImageName != null && oldImageName.lastIndexOf('.') > 0) {
            extension = oldImageName.substring(oldImageName.lastIndexOf('.') + 1);
        }
        String newImageName = newEmail + "." + extension;
        Path newImagePath = Paths.get(uploadDir, newImageName);

        if (Files.exists(oldImagePath)) {
            try {
                Files.move(oldImagePath, newImagePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
            }
        } else {
            System.out.println("File not exists in upload directory");
        }
        return newImageName;
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

}
