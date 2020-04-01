package net.petafuel.styx.api.validator;

import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExecutionRuleValidator implements ConstraintValidator<ValidateExecutionRule, String> {
    @Override
    public boolean isValid(String executionRule, ConstraintValidatorContext constraintValidatorContext) {
        try {
            PeriodicPayment.ExecutionRule.valueOf(executionRule);
            return true;
        } catch (IllegalArgumentException invalidEnumName) {
            return false;
        }
    }

}
