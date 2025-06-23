package com.backend.ems.Service.Service_Interface;

import org.springframework.web.multipart.MultipartFile;

import com.backend.ems.DTO.RegisterDTO;
import com.backend.ems.Entity.Register;

import java.io.IOException;

public interface OtpServiceInterface {
    void generateAndSendOtp(String email);

    Register verifyOtpAndRegister(RegisterDTO registerDTO, MultipartFile image) throws IOException;

    void resendOtp(String email);

    void verifyOtpAndUpdateEmail(Integer otp,String oldEmail,String newEmail);
}
