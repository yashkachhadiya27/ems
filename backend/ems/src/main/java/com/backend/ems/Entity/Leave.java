package com.backend.ems.Entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String reason;
    private LocalDate leaveFromDate;
    private LocalDate leaveToDate;
    private String leaveType;
    private String status;
    private LocalDate appliedOn;
    private LocalDate replyOn;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "register_id", referencedColumnName = "id")
    private Register register;

    private int wfhLeavesTaken;
    private int regularLeavesTaken;
    private int carryOverLeaves;
    private int lossOfPayDays;
}
