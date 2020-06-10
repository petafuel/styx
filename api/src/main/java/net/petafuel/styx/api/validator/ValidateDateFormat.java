package net.petafuel.styx.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateFormatValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidateDateFormat {
    String value() default "yyyy-MM-dd";

    String message() default "{net.petafuel.styx.api.validator.ValidateDateFormat.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
