package com.backend.ems.Exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {

    private int statusCode;
    private LocalDateTime timestamp;
    private String errMessage;
    private String status;
}
