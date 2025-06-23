package com.backend.ems.Exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String msg) {
        super(msg);
    }

}
