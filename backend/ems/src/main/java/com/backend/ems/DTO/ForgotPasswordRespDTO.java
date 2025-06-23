package com.backend.ems.DTO;

import lombok.Data;

@Data
public class ForgotPasswordRespDTO {
    boolean status;
    String email;
    String message;
}
