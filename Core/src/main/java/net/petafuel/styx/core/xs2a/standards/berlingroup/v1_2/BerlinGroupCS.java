package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BerlinGroupCS extends BasicService implements CSInterface {

    private static final MediaType JSON  = MediaType.get("application/json; charset=utf-8");

    private static final Logger LOG = LogManager.getLogger(BerlinGroupCS.class);

    public BerlinGroupCS(String url) {
        super(url);
    }

    public Consent createConsent(PSU psu, List<Account> balances, List<Account> transactions, UUID consentId) throws SignatureException, BankRequestFailedException {

        OkHttpClient client = new OkHttpClient();

        // build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest();
        createConsentRequest.setBalances(balances);
        createConsentRequest.setTransactions(transactions);

        // serialisation CreateConsentRequest
        Gson gson = new Gson();
        String json = gson.toJson(createConsentRequest);


        byte[] bytes;

        try {
             bytes = MessageDigest.getInstance("SHA-256").digest(json.getBytes());
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage());
            throw new SignatureException();
        }

        String digest = "SHA-256=" + Base64
                .getEncoder()
                .encodeToString(bytes);

        Map<String, String> headers = new LinkedHashMap<>();

        headers.put("psu-id", psu.getId());
        headers.put("x-request-id", consentId.toString());
        headers.put("digest", digest);
        headers.put("date", "2019-08-08"); // todo set date dynamic

        BerlinGroupSigner signer = new BerlinGroupSigner();
        headers = signer.sign(headers);

        RequestBody body = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder()
                .url(this.url + "/v1/consents")
                .post(body);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
        Request request = builder.build();

        // send Request
        try (Response response = client.newCall(request).execute()) {

            String responseBody = response.body().string();

            // Errors
            // check for required SCA
            // Common Errors (400, 500)
            if(response.code() != 201) {
                LOG.error(responseBody);
                throw new BankRequestFailedException();
            }

            // todo map Identifier (x-Request-Id, ID, consentId)
            CreateConsentResponse createConsentResponse = gson.fromJson(responseBody, CreateConsentResponse.class);

            // Response parsen
            return new Consent(consentId);
        } catch (IOException e) {
            throw new BankRequestFailedException();
        }

    }

    @Override
    public Consent getConsent() {
        return null;
    }

    @Override
    public Consent getStatus() {
        return null;
    }

    @Override
    public Consent deleteConsent() {
        return null;
    }
}
