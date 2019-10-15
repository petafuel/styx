package net.petafuel.styx.core.xs2a.oauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.oauth.http.TokenRequest;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.InitiatedPaymentSerializer;
import okhttp3.Response;

import java.util.UUID;

public class OAuthService extends BasicService {

    private static final String TOKEN_ENDPOINT = "/token";

    public OAuthService(String url, IBerlinGroupSigner signer) {
        super(url, signer);
    }

    public Token accessTokenRequest(TokenRequest request) throws BankRequestFailedException {

        this.setUrl(this.url + TOKEN_ENDPOINT);
        this.createBody(RequestType.POST, JSON, request);

        try (Response response = this.execute()) {
            String body = response.body().string();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Token.class, new TokenSerializer())
                    .create();
            return gson.fromJson(body, Token.class);
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
