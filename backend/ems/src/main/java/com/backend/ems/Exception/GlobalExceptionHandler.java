package com.backend.ems.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import io.jsonwebtoken.ExpiredJwtException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeExistsException.class)
    public ResponseEntity<?> handleEmployeeExistsException(EmployeeExistsException e) {
        ErrorDetails ed = new ErrorDetails(HttpStatus.CONFLICT.value(), LocalDateTime.now(), e.getMessage(),
                HttpStatus.CONFLICT.name());
        return new ResponseEntity<>(ed, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<?> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        ErrorDetails ed = new ErrorDetails(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), e.getMessage(),
                HttpStatus.NOT_FOUND.name());
        return new ResponseEntity<>(ed, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentValidException(MethodArgumentNotValidException e) {
        List<String> fielderrors = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        Map<String, Object> errormsg = new HashMap();
        errormsg.put("status", HttpStatus.BAD_REQUEST.name());
        errormsg.put("statusCode", HttpStatus.BAD_REQUEST.value());
        errormsg.put("timestamp", LocalDateTime.now());
        errormsg.put("errMessage", fielderrors);
        return new ResponseEntity<>(errormsg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomJWTException.class)
    public ResponseEntity<Object> handleCustomJwtException(CustomJWTException e) {
        ErrorDetails ed = new ErrorDetails(498, LocalDateTime.now(), e.getMessage(),
                HttpStatus.UNAUTHORIZED.name());
        return ResponseEntity.status(498).body(ed);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<Object> handleRefreshTokenExpiredException(RefreshTokenExpiredException e) {
        ErrorDetails ed = new ErrorDetails(498, LocalDateTime.now(), e.getMessage(),
                HttpStatus.FORBIDDEN.name());
        return ResponseEntity.status(498).body(ed);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        ErrorDetails ed = new ErrorDetails(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), e.getMessage(),
                HttpStatus.NOT_FOUND.name());
        return new ResponseEntity<>(ed, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleOtpNotFoundException(OtpNotFoundException e) {
        ErrorDetails ed = new ErrorDetails(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), e.getMessage(),
                HttpStatus.NOT_FOUND.name());
        return new ResponseEntity<>(ed, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorDetails> handleOtpExpiredException(OtpExpiredException e) {
        ErrorDetails ed = new ErrorDetails(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), e.getMessage(),
                HttpStatus.NOT_FOUND.name());
        return new ResponseEntity<>(ed, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleJWTExpirationException(Exception e) {
        ErrorDetails ed = new ErrorDetails(498, LocalDateTime.now(),
                e.getMessage(), "JWT Expired");
        return new ResponseEntity<>(ed, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e) {
        ErrorDetails ed = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now(),
                e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.name());
        return new ResponseEntity<>(ed, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.badRequest().body("File size exceeds the allowed limit.");
    }

}
