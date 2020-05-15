package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.entities.TransactionDeprecated;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;

import java.util.List;

/**
 * Account Information Service Interface
 *
 * @documented https://confluence.petafuel.intern/display/TOOL/Styx+Account+Information+Service+Interface
 * @see Account
 * @see XS2ARequest
 * @see BalanceContainer
 * @see TransactionDeprecated
 * @see TransactionContainer
 */
public interface AISInterface {

    /**
     * Returns an List of type Account which belong to the given Consent ID
     *
     * @param request The AIS get account list request
     * @return Returns a list of the possible accounts
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    List<AccountDetails> getAccountList(XS2ARequest request) throws BankRequestFailedException;

    /**
     * Returns the Account found by the given Account ID (path parameter resourceId)
     *
     * @param request XS2AGetRequest that should contain the Account ID
     * @return Returns a full Account object
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    AccountDetails getAccount(XS2ARequest request) throws BankRequestFailedException;

    /**
     * Returns a List of type Balance which belong to the given Account ID (path parameter account-id)
     *
     * @param request XS2AGetRequest which should contain the account id
     * @return returns balances with their linked account(optional)
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    BalanceContainer getBalancesByAccount(XS2ARequest request) throws BankRequestFailedException;

    /**
     * Returns a List of type Transaction which belong to the given Account ID (path parameter account-id)
     *
     * @param request XS2AGetRequest which should contain the account id
     * @return Returns a TransactionContainer which holds all the requested transactions
     * @throws BankRequestFailedException If the request towards the bank fails
     * @see AISInterface#getBalancesByAccount(XS2ARequest)
     */
    TransactionContainer getTransactionsByAccount(XS2ARequest request) throws BankRequestFailedException;

    /**
     * Returns the Transaction which belongs to the given Account ID and Transaction ID (path parameters account-id and transactionId)
     *
     * @param request XS2AGetRequest which should contain the account id and a certain transaction id
     * @return Returns a single Transaction object associated with the account id and transaction id
     * @throws BankRequestFailedException If the request towards the bank fails
     */
    TransactionDeprecated getTransaction(XS2ARequest request) throws BankRequestFailedException;
}
