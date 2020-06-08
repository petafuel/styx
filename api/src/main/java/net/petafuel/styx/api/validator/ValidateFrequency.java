package net.petafuel.styx.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FrequencyValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidateFrequency {
    String message() default "{net.petafuel.styx.api.validator.ValidateFrequency.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
