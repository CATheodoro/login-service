package com.theodoro.loginservice.api.rest.validators;

import com.theodoro.loginservice.api.rest.validators.annotations.PasswordValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.*;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        List<String> validationErrors = new ArrayList<>();

        if (password.length() < 8) {
            validationErrors.add(PASSWORD_LENGTH_BAD_REQUEST.getMessage());
        }

        if (!password.matches(".*[A-Z].*")) {
            validationErrors.add(PASSWORD_UPPERCASE_BAD_REQUEST.getMessage());
        }

        if (!password.matches(".*[a-z].*")) {
            validationErrors.add(PASSWORD_LOWERCASE_BAD_REQUEST.getMessage());
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            validationErrors.add(PASSWORD_SYMBOL_BAD_REQUEST.getMessage());
        }

        if (!password.matches(".*\\d.*")) {
            validationErrors.add(PASSWORD_DIGIT_BAD_REQUEST.getMessage());
        }

        if (!validationErrors.isEmpty()) {
            this.addConstraintViolation(context, validationErrors);
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, List<String> validationErrors) {
        context.disableDefaultConstraintViolation();
        for (String error : validationErrors){
            context.buildConstraintViolationWithTemplate(error)
                    .addConstraintViolation();
        }

    }
}