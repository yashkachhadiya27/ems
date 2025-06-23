package com.backend.ems.Service.Service_Interface;

import java.time.LocalDateTime;

import com.backend.ems.Entity.Register;

public interface TokenServiceInterface {
    public boolean validateAndRemoveExpiredRefreshToken(String refreshToken);

    public void saveRefreshToken(String refreshToken, LocalDateTime expirationDate, Register register);

    public void removeRefreshToken(String refreshToken);
}
