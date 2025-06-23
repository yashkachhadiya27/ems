package com.backend.ems.DTO;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotEmpty(message = "First name Should not be empty and Should not be null.")
    @Size(min = 3, max = 30, message = "Invalid First Name: Must be of 3 - 30 characters.")
    private String fname;

    @NotEmpty(message = "Middle name Should not be empty and Should not be null")
    @Size(min = 3, max = 30, message = "Invalid Middle Name: Must be of 3 - 30 characters.")
    private String mname;

    @NotEmpty(message = "Last name Should not be empty and Should not be null.")
    @Size(min = 3, max = 30, message = "Invalid Last Name: Must be of 3 - 30 characters.")
    private String lname;

    @Email(message = "Invalid Email.")
    private String email;

    @NotNull(message = "Gender should not be null.")
    private String gender;

    @NotEmpty(message = "Department Should not be empty and Should not be null.")
    private String department;

    @NotNull(message = "Joining date should not be null.")
    @PastOrPresent(message = "Joining date should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoining;

    @NotNull(message = "Birth date should not be null.")
    @Past(message = "Birth date value should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    private String password;

    @NotEmpty(message = "Phone number Should not be empty and Should not be null.")
    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    private String phone;

    @Column(columnDefinition = "TEXT[]")
    private String[] skills;

    @NotEmpty(message = "Street Should not be empty and Should not be null.")
    private String street;

    @Length(max = 6, message = "postal code length should be <= 10.")
    private String postalcode;

    @NotEmpty(message = "District Should not be empty and Should not be null.")
    private String district;

    @NotEmpty(message = "State Should not be empty and Should not be null.")
    private String state;

    @NotEmpty(message = "City Should not be empty and Should not be null.")
    private String city;

    @NotEmpty(message = "Country Should not be empty and Should not be null.")
    private String country;

    @NotEmpty(message = "OTP Should not be empty and Should not be null.")
    private String otpCode;

    private String experience;
}
