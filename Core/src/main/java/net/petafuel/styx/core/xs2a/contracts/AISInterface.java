package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Balance;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

public interface AISInterface {

    List<Account> getAccountList(XS2AGetRequest request) throws BankRequestFailedException;

    Account getAccount(XS2AGetRequest request) throws BankRequestFailedException;

    List<Balance> getBalancesByAccount(XS2AGetRequest request) throws BankRequestFailedException;

    List<Transaction> getTransactionsByAccount(XS2AGetRequest request) throws BankRequestFailedException;

    Transaction getTransaction(XS2AGetRequest request) throws BankRequestFailedException;
}
