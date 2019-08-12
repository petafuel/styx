package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Balance;
import net.petafuel.styx.core.xs2a.entities.Transaction;

import java.util.List;

public interface AISInterface {

    public List<Account> getAccountList();

    public Account getAccount(String accountId);

    public List<Balance> getBalancesByAccount(String accountId);

    public List<Transaction> getTransactionsByAccount(String accountId);

}
