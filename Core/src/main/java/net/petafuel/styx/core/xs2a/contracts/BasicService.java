package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.exceptions.CertificateException;
import net.petafuel.styx.core.xs2a.utils.CertificateManager;
import net.petafuel.styx.core.xs2a.standards.berlingroup.IBerlinGroupSigner;
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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;


public abstract class BasicService {
    protected static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    protected static final MediaType XML = MediaType.get("application/xml; charset=utf-8");
    private static final Logger LOG = LogManager.getLogger(BasicService.class);
    protected String url;
    private Request.Builder builder;
    private IBerlinGroupSigner signer;

    public BasicService(String url, IBerlinGroupSigner signer) {

        this.url = url;
        this.builder = new Request.Builder();
        this.signer = signer;
    }

    protected void setUrl(String url) {
        this.builder.url(url);
    }

    protected void createBody(RequestType requestType, MediaType mediaType, XS2ARequest request) {
        this.builder.method(requestType.name(), request != null ? RequestBody.create(request.getRawBody(), mediaType) : null);
    }

    protected void createBody(RequestType requestType) {
        this.builder.method(requestType.name(), null);
    }

    protected void createHeaders(XS2ARequest request) {
        // Create Headers
        XS2AHeaderParser.parse(request);

        //Sign Request
        this.signer.sign(request);

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
            LOG.error(e.getMessage());
        } catch (NullPointerException e) {
            LOG.error("Unable to get Trust Managers from TrustManager Factory");
        }
        if(x509Tm == null)
        {
            throw new CertificateException("There is no default Trust store available");
        }

        OkHttpClient client = new OkHttpClient().newBuilder().protocols(Arrays.asList(Protocol.HTTP_1_1)).sslSocketFactory(sslContext.getSocketFactory(), x509Tm).build();
        return client.newCall(request).execute();
    }

    protected enum RequestType {
        POST,
        GET,
        DELETE
    }

    private static String httpBuildQuery(Map<String, String> data) {
        if (data.isEmpty()) {
            return "";
        }
        StringJoiner query = new StringJoiner("&", "?", "");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            query.add(entry.getKey() + "=" + entry.getValue());
        }
        return query.toString();
    }

    protected String getHttpQueryString(XS2AGetRequest request) {
        XS2AQueryParameterParser.parse(request);
        return BasicService.httpBuildQuery(request.getQueryParameters());
    }
}
