package com.backend.ems.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

@Entity
@Table(name = "experience")
@Data
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotEmpty(message = "Name of Company should not be empty and should not be Null.")
    private String company;
   
    @NotEmpty(message = "Position should not be empty and should not be Null.")
    private String position;
   
    @NotNull(message = "Total Experince should not be empty and should not be Null.")
    private String totalExp;
   
    @NotNull(message = "Start Date should not be empty and should not be Null.")
    @Past(message = "Start date value should not contain future or current date.")
    private LocalDate startDate;
  
    @NotNull(message = "Start Date should not be empty and should not be Null.")
    @Past(message = "End date value should not contain future or current date.")
    private LocalDate endDate;
}
