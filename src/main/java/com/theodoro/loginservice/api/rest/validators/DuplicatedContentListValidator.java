package com.theodoro.loginservice.api.rest.validators;

import com.theodoro.loginservice.api.rest.validators.annotations.DuplicatedContentListValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.theodoro.loginservice.domains.enumerations.ExceptionMessagesEnum.DUPLICATED_FIELD_BAD_REQUEST;

public class DuplicatedContentListValidator implements ConstraintValidator<DuplicatedContentListValidation, List<String>> {

    @Override
    public boolean isValid(List<String> field, ConstraintValidatorContext context) {


        List<String> validationErrors = new ArrayList<>();

        Set<String> uniqueRoleCodes = new HashSet<>(field);

        if (uniqueRoleCodes.size() < field.size()) {
            validationErrors.add(DUPLICATED_FIELD_BAD_REQUEST.getMessage());
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