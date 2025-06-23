package com.backend.ems.Entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int register_id;

    @Column(columnDefinition = "dec(10,2)")
    private double basicPay;

    @Column(columnDefinition = "dec(10,2)")
    private double dearnessAllowance;

    @Column(columnDefinition = "dec(10,2)")
    private double houseRentAllowance;

    @Column(columnDefinition = "dec(10,2)")
    private double medicalAllowance;

    @Column(columnDefinition = "dec(10,2)")
    private double corporateAttireAllowance;

    @Column(columnDefinition = "dec(10,2)")
    private double regularBonus;

    @Column(columnDefinition = "dec(10,2)")
    private double taxDeducted;

    @Column(columnDefinition = "dec(10,2)")
    private double professionalTax;

    @Column(columnDefinition = "dec(10,2)")
    private double providentFund;

    @Temporal(TemporalType.DATE)
    private Date month;

    private boolean isPaid;

    @Temporal(TemporalType.DATE)
    private Date paidOn;
}
