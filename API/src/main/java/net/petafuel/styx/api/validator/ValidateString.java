package net.petafuel.styx.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StringValidator.class})
public @interface ValidateString {
    String[] allowedValues();

    String message() default "{net.petafuel.styx.api.validator.ValidateString.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
