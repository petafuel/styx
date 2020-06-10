package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;

import javax.json.bind.adapter.JsonbAdapter;

public class ExecutionRuleAdapter implements JsonbAdapter<PeriodicPayment.ExecutionRule, String> {

    @Override
    public String adaptToJson(PeriodicPayment.ExecutionRule executionRule) throws Exception {
        return executionRule.getValue();
    }

    @Override
    public PeriodicPayment.ExecutionRule adaptFromJson(String s) throws Exception {
        return PeriodicPayment.ExecutionRule.valueOf(s.toUpperCase());
    }
}
