package com.backend.ems.Service.Service_Interface;

import org.springframework.stereotype.Service;

import com.backend.ems.DTO.ForgotPasswordRespDTO;

@Service
public interface ForgotPasswordServiceInterface {
    public ForgotPasswordRespDTO verifyEmail(String email, String status);

    public ForgotPasswordRespDTO resendOTP(String email);

    public ForgotPasswordRespDTO verifyOTP(Integer otp, String email);

    public ForgotPasswordRespDTO changePassword(String newPassword, String email);
}
