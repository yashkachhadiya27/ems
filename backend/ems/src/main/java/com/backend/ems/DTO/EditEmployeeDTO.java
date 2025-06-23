package com.backend.ems.DTO;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditEmployeeDTO {
    @Size(min = 3, max = 30, message = "Invalid First Name: Must be of 3 - 30 characters.")
    private String fname;

    @Size(min = 3, max = 30, message = "Invalid Middle Name: Must be of 3 - 30 characters.")
    private String mname;

    @Size(min = 3, max = 30, message = "Invalid Last Name: Must be of 3 - 30 characters.")
    private String lname;

    @Email(message = "Invalid Email.")
    private String email;

    private String department;

    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    private String phone;

    @Past(message = "Birth date value should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @PastOrPresent(message = "Joining date should not contain future date.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoining;

}
