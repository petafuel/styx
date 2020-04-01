package net.petafuel.styx.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExecutionRuleValidator.class})
public @interface ValidateExecutionRule {
    String message() default "{net.petafuel.styx.api.validator.ValidateExecutionRule.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
