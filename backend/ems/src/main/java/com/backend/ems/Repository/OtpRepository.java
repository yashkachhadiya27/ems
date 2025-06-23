package com.backend.ems.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.ems.Entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByOtpCodeAndEmail(int otpCode, String email);

    Optional<Otp> findByEmail(String email);
}
