package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.xs2a.contracts.AISInterface;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Balance;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.contracts.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadBalancesRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.AccountSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.BalancesSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.TransactionsSerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BerlinGroupAIS extends BasicService implements AISInterface {

    private static final String GET_ACCOUNT_LIST = "/v1/accounts";
    private static final String GET_ACCOUNT_DETAILS = "/v1/accounts/%s";
    private static final String GET_BALANCES = "/v1/accounts/%s/balances";
    private static final String GET_TRANSACTIONS = "/v1/accounts/%s/transactions";
    private static final String GET_TRANSACTION_DETAILS = "/v1/accounts/%s/transactions/%s";

    private static final Logger LOG = LogManager.getLogger(BerlinGroupAIS.class);

    public BerlinGroupAIS(String url, IBerlinGroupSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public List<Account> getAccountList(XS2AGetRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + GET_ACCOUNT_LIST + this.getHttpQueryString(request));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {

            if (response.code() != 200) {
                throwBankRequestException(response);
            }
            String body = response.body().string();
            Type type = new TypeToken<ArrayList<Account>>() {
            }.getType();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(type, new AccountSerializer())
                    .create();

            return gson.fromJson(body, type);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Account getAccount(XS2AGetRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + GET_ACCOUNT_DETAILS + this.getHttpQueryString(request));

        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {

            if (response.code() != 200) {
                throwBankRequestException(response);
            }
            String body = response.body().string();
            Type type = new TypeToken<ArrayList<Account>>() {
            }.getType();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(type, new AccountSerializer())
                    .create();

            List<Account> accounts = gson.fromJson(body, type);
            return accounts.get(0);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public List<Balance> getBalancesByAccount(XS2AGetRequest request) throws BankRequestFailedException {

        this.setUrl(this.url + String.format(GET_BALANCES, ((ReadBalancesRequest) request).getAccountId()) + this.getHttpQueryString(request));

        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {

            if (response.code() != 200) {
                throwBankRequestException(response);
            }
            String body = response.body().string();
            Type type = new TypeToken<ArrayList<Balance>>() {
            }.getType();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(type, new BalancesSerializer())
                    .create();

            return gson.fromJson(body, type);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByAccount(XS2AGetRequest request) throws BankRequestFailedException {
        ReadTransactionsRequest r1 = (ReadTransactionsRequest) request;
        this.setUrl(this.url + String.format(GET_TRANSACTIONS, r1.getAccountId()) + this.getHttpQueryString(request));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {

            if (response.code() != 200) {
                throwBankRequestException(response);
            }
            String body = response.body().string();
            Type type = new TypeToken<ArrayList<Transaction>>() {
            }.getType();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(type, new TransactionsSerializer())
                    .create();

            return gson.fromJson(body, type);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public Transaction getTransaction(XS2AGetRequest request) throws BankRequestFailedException {

        ReadTransactionDetailsRequest r1 = (ReadTransactionDetailsRequest) request;
        this.setUrl(this.url + String.format(GET_TRANSACTION_DETAILS, r1.getAccountId(), r1.getTransactionId()));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {

            if (response.code() != 200) {
                throwBankRequestException(response);
            }
            String body = response.body().string();
            Type type = new TypeToken<ArrayList<Transaction>>() {
            }.getType();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(type, new TransactionsSerializer())
                    .create();

            List<Transaction> transactions = gson.fromJson(body, type);
            return transactions.get(0);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
