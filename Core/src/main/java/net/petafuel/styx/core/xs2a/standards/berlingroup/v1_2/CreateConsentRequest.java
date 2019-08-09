package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;



import net.petafuel.styx.core.xs2a.entities.Account;

import java.io.Serializable;
import java.util.List;

public class CreateConsentRequest implements Serializable {

    private class Access {
        public List<Account> balances;
        public List<Account> transactions;
    }

    private Access access = new Access();

    // todo set dynamic
    private boolean recurringIndicator = false;
    private String validUntil = "2019-10-10";
    private int frequencyPerDay = 1;
    private boolean combinedServiceIndicator = true;


    public void setBalances(List<Account> balances) {
        this.access.balances = balances;
    }

    public void setTransactions(List<Account> transactions) {
        this.access.transactions = transactions;
    }

}
