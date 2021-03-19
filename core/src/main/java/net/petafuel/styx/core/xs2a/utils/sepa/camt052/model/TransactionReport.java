package net.petafuel.styx.core.xs2a.utils.sepa.camt052.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class TransactionReport {
    @SerializedName("transactions")
    private List<TransactionDetailed> transactions = new ArrayList<>();

    public TransactionReport() {
        //Empty Constructor is needed for serialization purposes
    }

    public void add(TransactionDetailed transaction) {
        transactions.add(transaction);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
