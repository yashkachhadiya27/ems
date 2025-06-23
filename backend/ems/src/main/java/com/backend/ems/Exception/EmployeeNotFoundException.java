package com.backend.ems.Exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String msg) {
        super(msg);
    }
}
