package com.backend.ems.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDTO {
    private int empId;
    private String email;
    private String password;
    private String accessToken;
    private String refreshToken;
    private String role;
}
