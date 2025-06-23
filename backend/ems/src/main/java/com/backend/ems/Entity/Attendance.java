package com.backend.ems.Entity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "register_id")
    private Register register;

    private LocalDate attendanceDate;
    private boolean isPresent;
    private LocalDateTime lastCheckInTime;
    private Long totalInTime;
    private Long totalBreakTime;

    public Attendance() {
        this.totalInTime = 0L;
        this.totalBreakTime = 0L;
        this.attendanceDate = LocalDate.now();
        this.isPresent = false;
    }

    public Duration getTotalInTime() {
        return Duration.ofSeconds(totalInTime != null ? totalInTime : 0L);
    }

    public void setTotalInTime(Duration duration) {
        this.totalInTime = duration != null ? duration.getSeconds() : 0L;
    }

    public Duration getTotalBreakDuration() {
        return Duration.ofSeconds(this.totalBreakTime);
    }

    public void setTotalBreakTime(Duration duration) {
        this.totalBreakTime = duration.getSeconds();
    }
}
