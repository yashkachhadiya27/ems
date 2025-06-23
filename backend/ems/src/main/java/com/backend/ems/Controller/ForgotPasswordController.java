package com.backend.ems.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.DTO.ForgotPasswordRespDTO;
import com.backend.ems.Service.Service_implementation.ForgotPasswordServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class ForgotPasswordController {
    private final ForgotPasswordServiceImpl forgotPasswordServiceImpl;

    @GetMapping("/verifyEmail/{email}/{status}")
    public ResponseEntity<ForgotPasswordRespDTO> verifyEmployeeEmail(@PathVariable String email,
            @PathVariable String status) {
        ForgotPasswordRespDTO forgotPasswordRespDTO = forgotPasswordServiceImpl.verifyEmail(email, status);
        return ResponseEntity.ok(forgotPasswordRespDTO);
    }

    @GetMapping("/verifyOTP/{otp}/{email}")
    public ResponseEntity<ForgotPasswordRespDTO> verifyOTP(@PathVariable Integer otp, @PathVariable String email) {
        return ResponseEntity.ok(forgotPasswordServiceImpl.verifyOTP(otp, email));
    }

    @PutMapping("/changePassword/{email}")
    public ResponseEntity<ForgotPasswordRespDTO> updateEmployeePassword(@RequestParam("newPassword") String newPassword,
            @PathVariable String email) {
        return ResponseEntity.ok(forgotPasswordServiceImpl.changePassword(newPassword, email));
    }

    @GetMapping("/resendForgotOTP/{email}")
    public ResponseEntity<ForgotPasswordRespDTO> resendForgotPasswordOTP(@PathVariable String email) {
        return ResponseEntity.ok(forgotPasswordServiceImpl.resendOTP(email));
    }
}
