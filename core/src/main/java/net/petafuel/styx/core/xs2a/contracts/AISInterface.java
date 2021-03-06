package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

/**
 * Account Information Service Interface
 *
 * @see XS2ARequest
 * @see BalanceContainer
 * @see TransactionContainer
 * @see AccountDetails
 */
public interface AISInterface {

    /**
     * Returns an List of type Account which belong to the given Consent ID
     *
     * @param request The AIS get account list request
     * @return Returns a list of the possible accounts
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    List<AccountDetails> getAccountList(AISRequest request) throws BankRequestFailedException;

    /**
     * Returns the Account found by the given Account ID (path parameter resourceId)
     *
     * @param request XS2AGetRequest that should contain the Account ID
     * @return Returns a full Account object
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    AccountDetails getAccount(AISRequest request) throws BankRequestFailedException;

    /**
     * Returns a List of type Balance which belong to the given Account ID (path parameter account-id)
     *
     * @param request XS2AGetRequest which should contain the account id
     * @return returns balances with their linked account(optional)
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    BalanceContainer getBalancesByAccount(AISRequest request) throws BankRequestFailedException;

    /**
     * Returns a List of type Transaction which belong to the given Account ID (path parameter account-id)
     *
     * @param request XS2AGetRequest which should contain the account id
     * @return Returns a TransactionContainer which holds all the requested transactions
     * @throws BankRequestFailedException If the request towards the bank fails
     * @see AISInterface#getBalancesByAccount(AISRequest)
     */
    TransactionContainer getTransactionsByAccount(AISRequest request) throws BankRequestFailedException;

    /**
     * Returns the Transaction which belongs to the given Account ID and Transaction ID (path parameters account-id and transactionId)
     *
     * @param request XS2AGetRequest which should contain the account id and a certain transaction id
     * @return Returns a single Transaction object associated with the account id and transaction id
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    Transaction getTransaction(AISRequest request) throws BankRequestFailedException;
}
