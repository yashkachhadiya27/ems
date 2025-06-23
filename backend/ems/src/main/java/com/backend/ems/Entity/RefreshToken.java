package com.backend.ems.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refreshToken")
@Data
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String jwtSecretToken;

    @OneToOne
    @JoinColumn(name = "register_id")
    @JsonIgnore
    private Register register;

    private LocalDateTime expirationTime;

    public RefreshToken(String jwtSecretToken, Register register, LocalDateTime expirationTime) {
        this.jwtSecretToken = jwtSecretToken;
        this.register = register;
        this.expirationTime = expirationTime;
    }

}
