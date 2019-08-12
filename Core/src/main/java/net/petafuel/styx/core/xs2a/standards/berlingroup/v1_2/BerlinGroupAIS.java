package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.AISInterface;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Balance;
import net.petafuel.styx.core.xs2a.entities.Transaction;

import java.util.List;

public class BerlinGroupAIS extends BasicService implements AISInterface {

    public BerlinGroupAIS(String url) {
        super(url);
    }

    @Override
    public List<Account> getAccountList() {
        return null;
    }

    @Override
    public Account getAccount(String accountId) {
        return null;
    }

    @Override
    public List<Balance> getBalancesByAccount(String accountId) {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(String accountId) {
        return null;
    }
}
