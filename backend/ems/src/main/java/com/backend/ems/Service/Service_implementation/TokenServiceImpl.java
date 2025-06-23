package com.backend.ems.Service.Service_implementation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.backend.ems.Entity.RefreshToken;
import com.backend.ems.Entity.Register;
import com.backend.ems.Exception.RefreshTokenExpiredException;
import com.backend.ems.Exception.RefreshTokenNotFoundException;
import com.backend.ems.Repository.RefreshTokenRepository;
import com.backend.ems.Service.Service_Interface.TokenServiceInterface;
import com.backend.ems.Util.JWTUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenServiceInterface {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtils jwtUtils;

    @Override
    public boolean validateAndRemoveExpiredRefreshToken(String refreshToken) {
        if (!refreshTokenRepository.existsByJwtSecretToken(refreshToken)) {
            return false;
        }
        boolean isValid = jwtUtils.isRefreshTokenValid(refreshToken, null);
        if (!isValid) {
            refreshTokenRepository.deleteByJwtSecretToken(refreshToken);
            throw new RefreshTokenExpiredException("Refresh token has expired and has been removed.");
        }
        return isValid;
    }

    @Override
    public void saveRefreshToken(String refreshToken, LocalDateTime expirationTime, Register register) {
        RefreshToken token = refreshTokenRepository.findByRegister(register)
                .orElse(new RefreshToken());
        token.setJwtSecretToken(refreshToken);
        token.setExpirationTime(expirationTime);
        token.setRegister(register);
        refreshTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void removeRefreshToken(String refreshToken) {
        if (refreshTokenRepository.existsByJwtSecretToken(refreshToken)) {
            // System.out.println(refreshTokenRepository.existsByJwtSecretToken(refreshToken));
            refreshTokenRepository.deleteByJwtSecretToken(refreshToken);
            // System.out.println(refreshTokenRepository.existsByJwtSecretToken(refreshToken));

        } else {
            throw new RefreshTokenNotFoundException("Refresh token not found or already removed.");
        }
    }

}
