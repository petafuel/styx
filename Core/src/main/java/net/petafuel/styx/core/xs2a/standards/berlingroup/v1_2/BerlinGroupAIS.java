package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.AISInterface;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountDetailsResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountListResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionDetailsResponse;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;

public class BerlinGroupAIS extends BasicService implements AISInterface {
    private static final Logger LOG = LogManager.getLogger(BerlinGroupAIS.class);

    public BerlinGroupAIS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public List<AccountDetails> getAccountList(AISRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + request.getServicePath() + this.getHttpQueryString(request));
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);
            return jsonb.fromJson(responseBody, ReadAccountListResponse.class).getAccounts();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public AccountDetails getAccount(AISRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + request.getServicePath());

        this.createBody(RequestType.GET);
        this.createHeaders(request);
        AccountDetails accountDetails;
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
    public BalanceContainer getBalancesByAccount(AISRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + request.getServicePath() + this.getHttpQueryString(request));
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
    public TransactionContainer getTransactionsByAccount(AISRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + request.getServicePath() + this.getHttpQueryString(request));
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
    public Transaction getTransaction(AISRequest request) throws BankRequestFailedException {
        this.setUrl(this.url + request.getServicePath());
        this.createBody(RequestType.GET);
        this.createHeaders(request);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 200);

            return jsonb.fromJson(responseBody, ReadTransactionDetailsResponse.class).getTransactionsDetails();
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
