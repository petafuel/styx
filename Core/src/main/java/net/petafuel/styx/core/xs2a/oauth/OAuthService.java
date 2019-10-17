package net.petafuel.styx.core.xs2a.oauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.oauth.entities.Token;
import net.petafuel.styx.core.xs2a.oauth.http.TokenRequest;
import net.petafuel.styx.core.xs2a.oauth.serializers.TokenSerializer;
import net.petafuel.styx.core.xs2a.contracts.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupAIS;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class OAuthService extends BasicService {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupAIS.class);

    private static final String TOKEN_ENDPOINT = "/token";

    public OAuthService(String url, IBerlinGroupSigner signer) {
        super(LOG, url, signer);
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

    public static String generateState() {
        return UUID.randomUUID().toString();
    }

    public static String generateCodeVerifier() {

        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {

        byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        return  Base64.getUrlEncoder().encodeToString(digest);
    }
}
