package net.petafuel.styx.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {TokenTypeValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidateTokenType {
    String message() default "{net.petafuel.styx.api.validator.ValidateTokenType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
