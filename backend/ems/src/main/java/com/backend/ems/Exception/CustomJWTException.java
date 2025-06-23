package com.backend.ems.Exception;

public class CustomJWTException extends RuntimeException {
    public CustomJWTException(String msg) {
        super(msg);
    }
}
