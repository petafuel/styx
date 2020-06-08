package net.petafuel.styx.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringValidator implements ConstraintValidator<ValidateString, String> {
    private List<String> valueList;

    @Override
    public void initialize(ValidateString constraintAnnotation) {
        valueList = new ArrayList<>();
        valueList.addAll(Arrays.asList(constraintAnnotation.allowedValues()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            //nothing to validate, use @NotNull
            return true;
        }
        return valueList.contains(value);
    }
}
