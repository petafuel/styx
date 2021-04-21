package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.TransactionStatus;

import javax.json.bind.adapter.JsonbAdapter;

public class TransactionStatusTypeAdapter implements JsonbAdapter<TransactionStatus, String> {

    @Override
    public String adaptToJson(TransactionStatus value) {
        return value.toString();
    }

    @Override
    public TransactionStatus adaptFromJson(String s) {
        return TransactionStatus.getValue(s);
    }
}
