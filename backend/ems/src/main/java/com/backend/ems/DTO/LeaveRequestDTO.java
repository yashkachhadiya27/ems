package com.backend.ems.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.backend.ems.Annotation.ValidLeaveDates;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@ValidLeaveDates
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaveRequestDTO {
    @NotEmpty(message = "Reason Should not be empty and Should not be null.")
    private String reason;
    @Future(message = "LeaveFromDate should not contain future date.")
    private LocalDate leaveFromDate;
    @Future(message = "LeaveToDate should not contain future date.")
    private LocalDate leaveToDate;

    @NotEmpty(message = "Leave Type Should not be empty and Should not be null.")
    private String leaveType;
    private String status;
    private LocalDateTime appliedOn;
    private LocalDateTime replyOn;
}
