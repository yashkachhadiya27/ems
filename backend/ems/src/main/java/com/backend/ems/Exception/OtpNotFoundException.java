package com.backend.ems.Exception;

public class OtpNotFoundException extends RuntimeException {
    public OtpNotFoundException(String msg) {
        super(msg);
    }
}
