package net.petafuel.styx.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatValidator implements ConstraintValidator<ValidateDateFormat, String> {
    private String dateFormat;

    @Override
    public void initialize(ValidateDateFormat constraintAnnotation) {
        dateFormat = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            //nothing to validate, use @NotNull
            return true;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(value);
            return true;
        } catch (ParseException | IllegalArgumentException e) {
            return false;
        }
    }
}
