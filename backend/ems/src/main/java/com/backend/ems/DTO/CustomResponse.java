package com.backend.ems.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CustomResponse {
   
    private String message;
    private int code;
    public CustomResponse(String message, int code) {
        this.message=message;
        this.code=code;
    }
}
