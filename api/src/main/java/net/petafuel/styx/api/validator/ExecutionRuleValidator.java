package net.petafuel.styx.api.validator;

import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExecutionRuleValidator implements ConstraintValidator<ValidateExecutionRule, String> {
    @Override
    public boolean isValid(String executionRule, ConstraintValidatorContext constraintValidatorContext) {
        if(executionRule == null)
        {
            //do not validate if there was no execution Rule specified within the request
            return true;
        }
        try {
            PeriodicPayment.ExecutionRule.valueOf(executionRule);
            return true;
        } catch (IllegalArgumentException invalidEnumName) {
            return false;
        }
    }

}
