package com.backend.ems.Annotation;

import com.backend.ems.DTO.LeaveRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LeaveDatesValidator implements ConstraintValidator<ValidLeaveDates, LeaveRequestDTO> {
    @Override
    public boolean isValid(LeaveRequestDTO entity, ConstraintValidatorContext context) {
        if (entity.getLeaveFromDate() == null || entity.getLeaveToDate() == null) {
            return true;
        }

        return (entity.getLeaveToDate().isAfter(entity.getLeaveFromDate())
                || entity.getLeaveToDate().isEqual(entity.getLeaveFromDate()));
    }
}
