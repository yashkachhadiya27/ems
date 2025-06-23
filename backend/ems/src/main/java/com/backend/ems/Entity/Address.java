package com.backend.ems.Entity;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    public Address(String street, String postalcode, String district, String city, String country,String state) {
        this.street = street;
        this.postalcode = postalcode;
        this.city = city;
        this.district = district;
        this.state=state;
        this.country = country;
    }

}
