package com.backend.ems.Exception;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(String msg) {
        super(msg);
    }
}
