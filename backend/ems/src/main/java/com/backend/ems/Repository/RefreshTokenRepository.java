package com.backend.ems.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import com.backend.ems.Entity.RefreshToken;
import com.backend.ems.Entity.Register;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.jwtSecretToken = :jwtSecretToken")
    void deleteByJwtSecretToken(@Param("jwtSecretToken") String jwtSecretToken);

    boolean existsByJwtSecretToken(String jwtSecretToken);

    Optional<RefreshToken> findByRegister(Register register);

    RefreshToken findByJwtSecretToken(String refreshToken);
}
