package com.backend.ems.DTO;

import lombok.Data;

@Data
public class AccessTokenResponse {
    private String accessToken;

    public AccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
