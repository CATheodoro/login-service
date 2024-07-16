package com.theodoro.loginservice.api.rest.validators.annotations;

import com.theodoro.loginservice.api.rest.validators.DuplicatedContentListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = DuplicatedContentListValidator.class)
@Documented
public @interface DuplicatedContentListValidation {
    String message() default "Code is mandatory";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
