package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.CertificateException;
import net.petafuel.styx.core.xs2a.utils.CertificateManager;
import net.petafuel.styx.core.xs2a.utils.XS2AHeaderParser;
import net.petafuel.styx.core.xs2a.utils.XS2AQueryParameterParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;


public abstract class BasicService {
    protected static final MediaType JSON = MediaType.get("application/json");
    protected static final MediaType XML = MediaType.get("application/xml;charset=utf-8");
    protected static final MediaType FORM_URLENCODED = MediaType.get("application/x-www-form-urlencoded");
    private final Logger logger;
    protected String url;
    private final Request.Builder builder;
    private final IXS2AHttpSigner signer;

    public BasicService(String url, IXS2AHttpSigner signer) {
        this(LogManager.getLogger(BasicService.class), url, signer);
    }

    public BasicService(Logger log, String url, IXS2AHttpSigner signer) {
        logger = log;

        this.url = url;
        this.builder = new Request.Builder();
        this.signer = signer;
    }

    public static String httpBuildQuery(Map<String, String> data) {
        if (data.isEmpty()) {
            return "";
        }
        StringJoiner query = new StringJoiner("&", "?", "");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            query.add(entry.getKey() + "=" + entry.getValue());
        }
        return query.toString();
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        this.builder.url(url);
    }

    public IXS2AHttpSigner getSigner() {
        return signer;
    }

    protected void createBody(RequestType requestType, MediaType mediaType, XS2ARequest request) {
        if (!requestType.equals(RequestType.GET) && !request.getRawBody().isPresent()) {
            logger.warn("Sending empty request body for non GET http-method");
        }
        // RequestBody.create() method would add charset to media type automatically if signature (String, MediaType)
        // if signature (byte[], MediaType) media is used as supplied
        createBody(requestType, RequestBody.create(request.getRawBody().orElse("").getBytes(StandardCharsets.UTF_8), mediaType));
    }

    protected void createBody(RequestType requestType, RequestBody body) {
        this.builder.method(requestType.name(), body);
    }

    protected void createBody(RequestType requestType) {
        this.builder.method(requestType.name(), null);
    }

    protected void createHeaders(XS2ARequest request) {
        // Create Headers
        XS2AHeaderParser.parse(request);

        //Sign request if there was a Signer class specified for the Service
        if (this.signer != null) {
            this.signer.sign(request);
        }

        // Set Request Headers
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
    }

    protected Response execute() throws IOException {
        Request request = this.builder.build();
        CertificateManager certificateManager = CertificateManager.getInstance();
        SSLContext sslContext = certificateManager.getSSLContext();

        //TODO Refactor the trustmanager into the CertificateManager
        X509TrustManager x509Tm = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init((KeyStore) null);
            for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    x509Tm = (X509TrustManager) tm;
                    break;
                }
            }
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            logger.error("Unable to execute request", e);
        } catch (NullPointerException e) {
            logger.error("Unable to get Trust Managers from TrustManager Factory", e);
        }
        if (x509Tm == null) {
            throw new CertificateException("There is no default Trust store available");
        }

        OkHttpClient client = new OkHttpClient().newBuilder().protocols(Collections.singletonList(Protocol.HTTP_1_1)).sslSocketFactory(sslContext.getSocketFactory(), x509Tm).build();
        return client.newCall(request).execute();
    }

    protected String getHttpQueryString(XS2ARequest request) {
        XS2AQueryParameterParser.parse(request);
        return BasicService.httpBuildQuery(request.getQueryParameters());
    }

    protected String extractResponseBody(Response response, int expectedResponseCode) throws BankRequestFailedException, IOException {
        return extractResponseBody(response, expectedResponseCode, true);
    }

    protected String extractResponseBody(Response response, int expectedResponseCode, boolean expectBody) throws BankRequestFailedException, IOException {
        String responseBody = response.body() != null ? response.body().string() : null;

        String responseContentLength = response.header("Content-Length");
        String originalRequestURL = response.request().url().toString();

        if ((expectBody && responseBody == null) || response.code() != expectedResponseCode) {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                logger.error("Bank request failed with empty body contentLength={}, httpcode={}, requestUrl={}", responseContentLength, response.code(), originalRequestURL);
                throw new BankRequestFailedException("empty response body", response.code());
            } else {
                logger.error("Bank request failed with contentLength={}, code={}, body={}, requestUrl={}", responseContentLength, response.code(), responseBody, originalRequestURL);
                throw new BankRequestFailedException(responseBody, response.code());
            }
        }
        return responseBody;
    }

    /**
     * add a header to builder
     * @param key headerKey
     * @param value headerValue
     */
    protected void addHeader(String key, String value){
        this.builder.header(key, value);
    }

    public enum RequestType {
        POST,
        GET,
        DELETE,
        PUT
    }
}
