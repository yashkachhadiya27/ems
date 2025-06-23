package com.backend.ems.Service.Service_implementation;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.ems.DTO.ForgotPasswordRespDTO;
import com.backend.ems.Entity.Otp;
import com.backend.ems.Entity.Register;
import com.backend.ems.Repository.OtpRepository;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.Service_Interface.ForgotPasswordServiceInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordServiceInterface {
    private final RegisterRepository registerRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpServiceImpl otpServiceImpl;

    @Override
    public ForgotPasswordRespDTO verifyEmail(String email, String status) {
        ForgotPasswordRespDTO forgotPassDTO = new ForgotPasswordRespDTO();

        if (status.equalsIgnoreCase("new")) {
            otpServiceImpl.generateAndSendOtp(email);
            forgotPassDTO.setEmail(email);
            forgotPassDTO.setStatus(true);
            forgotPassDTO.setMessage("OTP Sent successfully to " + email);
            return forgotPassDTO;
        } else {
            Register reg = registerRepository.findByEmail(email).orElse(null);

            if (reg == null) {

                forgotPassDTO.setEmail(email);
                forgotPassDTO.setStatus(false);
                forgotPassDTO.setMessage("User not found.");
                return forgotPassDTO;
            }
            otpServiceImpl.generateAndSendOtp(email);
            forgotPassDTO.setEmail(email);
            forgotPassDTO.setStatus(true);
            forgotPassDTO.setMessage("OTP Sent successfully to " + email);
            return forgotPassDTO;
        }
    }

    @Override
    public ForgotPasswordRespDTO verifyOTP(Integer otp, String email) {
        Otp otp1 = otpRepository
                .findByOtpCodeAndEmail(otp, email).orElse(null);
        ForgotPasswordRespDTO forgotPassDto = new ForgotPasswordRespDTO();
        if (otp1 == null || otp1.getExpirationTime().isBefore(LocalDateTime.now())) {
            forgotPassDto.setEmail(email);
            forgotPassDto.setStatus(false);
            forgotPassDto.setMessage("Invalid OTP or OTP has been Expired");
            return forgotPassDto;
        }
        forgotPassDto.setEmail(email);
        forgotPassDto.setStatus(true);
        forgotPassDto.setMessage("OTP Verified.");
        otpRepository.deleteById(otp1.getId());
        return forgotPassDto;

    }

    @Override
    public ForgotPasswordRespDTO changePassword(String newPassword, String email) {
        ForgotPasswordRespDTO fpResponseDto = new ForgotPasswordRespDTO();
        registerRepository.updatePassword(email, passwordEncoder.encode(newPassword));
        fpResponseDto.setEmail(email);
        fpResponseDto.setStatus(true);
        fpResponseDto.setMessage("Success");
        return fpResponseDto;

    }

    @Override
    public ForgotPasswordRespDTO resendOTP(String email) {
        otpServiceImpl.resendOtp(email);
        ForgotPasswordRespDTO forgotPasswordRespDTO = new ForgotPasswordRespDTO();
        forgotPasswordRespDTO.setStatus(true);
        forgotPasswordRespDTO.setEmail(email);
        forgotPasswordRespDTO.setMessage("New OTP sent");
        return forgotPasswordRespDTO;
    }

}
