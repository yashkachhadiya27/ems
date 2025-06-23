package com.backend.ems.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleOAuthUserDto {
    private String name;
    private String image;
    private String email;

}
