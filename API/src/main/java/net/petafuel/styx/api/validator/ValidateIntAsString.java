package net.petafuel.styx.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IntAsStringValidator.class})
public @interface ValidateIntAsString {
    int max() default Integer.MAX_VALUE;

    int min() default Integer.MIN_VALUE;

    String message() default "{net.petafuel.styx.api.validator.ValidateIntAsString.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
