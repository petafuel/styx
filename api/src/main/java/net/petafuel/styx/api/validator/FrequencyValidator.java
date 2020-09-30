package net.petafuel.styx.api.validator;

import net.petafuel.styx.core.xs2a.entities.Frequency;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FrequencyValidator implements ConstraintValidator<ValidateFrequency, String> {
    @Override
    public boolean isValid(String frequency, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Frequency.valueOf(frequency);
            return true;
        } catch (IllegalArgumentException invalidEnumName) {
            return false;
        }
    }

}

