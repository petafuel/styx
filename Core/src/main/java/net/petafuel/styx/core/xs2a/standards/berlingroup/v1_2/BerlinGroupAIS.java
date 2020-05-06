package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.xs2a.contracts.AISInterface;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.entities.TransactionDeprecated;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountDetailsResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadBalancesRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers.AccountSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.TransactionsSerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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

    public BerlinGroupAIS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public List<Account> getAccountList(XS2ARequest request) throws BankRequestFailedException {
        this.setUrl(this.url + GET_ACCOUNT_LIST + this.getHttpQueryString(request));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {

            String responseBody = extractResponseBody(response, 200);
            Type type = new TypeToken<ArrayList<Account>>() {
            }.getType();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(type, new AccountSerializer())
                    .create();

            return gson.fromJson(responseBody, type);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public AccountDetails getAccount(XS2ARequest request) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_ACCOUNT_DETAILS, ((ReadAccountDetailsRequest) request).getAccountId()) + this.getHttpQueryString(request));

        this.createBody(RequestType.GET);
        this.createHeaders(request);
        AccountDetails accountDetails = null;
        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            accountDetails = jsonb.fromJson(responseBody, ReadAccountDetailsResponse.class).getAccount();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }

        if (accountDetails == null) {
            throw new SerializerException("Unable to deserialize account details response body to AccountDetails object");
        } else {
            return accountDetails;
        }
    }

    @Override
    public BalanceContainer getBalancesByAccount(XS2ARequest request) throws BankRequestFailedException {
        this.setUrl(this.url + String.format(GET_BALANCES, ((ReadBalancesRequest) request).getAccountId()) + this.getHttpQueryString(request));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            return jsonb.fromJson(responseBody, BalanceContainer.class);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public TransactionContainer getTransactionsByAccount(XS2ARequest request) throws BankRequestFailedException {
        ReadTransactionsRequest r1 = (ReadTransactionsRequest) request;
        this.setUrl(this.url + String.format(GET_TRANSACTIONS, r1.getAccountId()) + this.getHttpQueryString(request));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            return jsonb.fromJson(responseBody, TransactionContainer.class);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public TransactionDeprecated getTransaction(XS2ARequest request) throws BankRequestFailedException {

        ReadTransactionDetailsRequest r1 = (ReadTransactionDetailsRequest) request;
        this.setUrl(this.url + String.format(GET_TRANSACTION_DETAILS, r1.getAccountId(), r1.getTransactionId()));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute()) {

            String responseBody = extractResponseBody(response, 200);
            Type type = new TypeToken<ArrayList<TransactionDeprecated>>() {
            }.getType();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(type, new TransactionsSerializer())
                    .create();

            List<TransactionDeprecated> transactions = gson.fromJson(responseBody, type);
            return transactions.get(0);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
