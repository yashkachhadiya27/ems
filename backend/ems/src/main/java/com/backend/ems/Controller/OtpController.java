package com.backend.ems.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.ems.Service.Service_implementation.OtpServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class OtpController {
    private final OtpServiceImpl otpServiceImpl;

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<HttpStatus> sendOtp(@PathVariable String email) {
        otpServiceImpl.generateAndSendOtp(email);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/resend-otp/{email}")
    public ResponseEntity<String> resendOtp(@PathVariable String email) {
        otpServiceImpl.resendOtp(email);
        return ResponseEntity.ok("New OTP sent to " + email);
    }

    
}
