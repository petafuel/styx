package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Balance;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

/**
 * Account Information Service Interface
 *
 * @see Account
 * @see XS2AGetRequest
 * @see Balance
 * @see Transaction
 */
public interface AISInterface {

    /**
     * Returns an List of type Account which belong to the given Consent ID
     *
     * @param request The AIS get account list request
     * @return Returns a list of the possible accounts
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    List<Account> getAccountList(XS2AGetRequest request) throws BankRequestFailedException;

    /**
     * Returns the Account found by the given Account ID (path parameter resourceId)
     *
     * @param request XS2AGetRequest that should contain the Account ID
     * @return Returns a full Account object
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    Account getAccount(XS2AGetRequest request) throws BankRequestFailedException;

    /**
     * Returns a List of type Balance which belong to the given Account ID (path parameter account-id)
     *
     * @param request XS2AGetRequest which should contain the account id
     * @return Returns a list of Balance objects associated with the given account id
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    List<Balance> getBalancesByAccount(XS2AGetRequest request) throws BankRequestFailedException;

    /**
     * Returns a List of type Transaction which belong to the given Account ID (path parameter account-id)
     *
     * @see AISInterface#getBalancesByAccount(XS2AGetRequest)
     * @param request XS2AGetRequest which should contain the account id
     * @return Returns a list of Transaction objects associated with the given account id
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    List<Transaction> getTransactionsByAccount(XS2AGetRequest request) throws BankRequestFailedException;

    /**
     * Returns the Transaction which belongs to the given Account ID and Transaction ID (path parameters account-id and transactionId)
     *
     * @param request XS2AGetRequest which should contain the account id and a certain transaction id
     * @return Returns a single Transaction object associated with the account id and transaction id
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    Transaction getTransaction(XS2AGetRequest request) throws BankRequestFailedException;
}
