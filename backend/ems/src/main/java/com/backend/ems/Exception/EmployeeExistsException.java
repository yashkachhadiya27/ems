package com.backend.ems.Exception;

public class EmployeeExistsException extends RuntimeException {
    public EmployeeExistsException(String msg) {
        super(msg);
    }
}
