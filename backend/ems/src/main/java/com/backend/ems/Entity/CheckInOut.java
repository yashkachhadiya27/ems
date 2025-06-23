package com.backend.ems.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
public class CheckInOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @Column(name = "duration_in_seconds")
    private Long durationInSeconds;

    public Duration getDuration() {
        return Duration.ofSeconds(this.durationInSeconds != null ? this.durationInSeconds : 0L);
    }

    public void setDuration(Duration duration) {
        this.durationInSeconds = duration != null ? duration.getSeconds() : 0L;
    }
}
