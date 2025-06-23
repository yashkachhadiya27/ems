package com.backend.ems.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LeaveDatesValidator.class)
public @interface ValidLeaveDates {
    String message() default "Leave To Date should be after Leave From Date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
