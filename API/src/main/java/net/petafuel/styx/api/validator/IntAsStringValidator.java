package net.petafuel.styx.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IntAsStringValidator implements ConstraintValidator<ValidateIntAsString, String> {
    int maxValue;
    int minValue;

    @Override
    public void initialize(ValidateIntAsString constraintAnnotation) {
        maxValue = constraintAnnotation.max();
        minValue = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            int intVal = Integer.parseInt(value);
            return intVal <= maxValue && intVal >= minValue;
        } catch (NumberFormatException invalidDataType) {
            return false;
        }
    }
}
