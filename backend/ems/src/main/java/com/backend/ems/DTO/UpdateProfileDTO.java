package com.backend.ems.DTO;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDTO {

    @Size(min = 3, max = 30, message = "Invalid First Name: Must be of 3 - 30 characters.")
    private String fname;

    @Size(min = 3, max = 30, message = "Invalid Middle Name: Must be of 3 - 30 characters.")
    private String mname;

    @Size(min = 3, max = 30, message = "Invalid Last Name: Must be of 3 - 30 characters.")
    private String lname;

    private String gender;

    private String department;

    @PastOrPresent(message = "Joining date should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoining;

    @Past(message = "Birth date value should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    private String phone;

    private String[] skills;

    private String street;

    @Length(max = 6, message = "postal code length should be <= 10.")
    private String postalcode;

    private String district;

    private String city;

    private String state;

    private String country;

}
