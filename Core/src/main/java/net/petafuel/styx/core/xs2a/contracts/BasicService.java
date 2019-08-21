package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.BerlinGroupSigner;
import net.petafuel.styx.core.xs2a.utils.XS2AHeaderParser;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public abstract class BasicService {

    protected static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    protected static final MediaType XML = MediaType.get("text/xml; charset=utf-8");
    protected String url;
    private Request.Builder builder;
    public BasicService(String url) {

        this.url = url;
        this.builder = new Request.Builder();
    }

    protected void setUrl(String url) {
        this.builder.url(url);
    }

    protected void createBody(RequestType requestType, MediaType mediaType, XS2ARequest request) {
        this.builder.method(requestType.name(), RequestBody.create(request.getRawBody(), mediaType));
    }

    protected void createHeaders(XS2ARequest request) {
        // Create Headers
        XS2AHeaderParser.parse(request);

        //Sign Request
        BerlinGroupSigner berlinGroupSigner = new BerlinGroupSigner();
        berlinGroupSigner.sign(request);

        // Set Request Headers
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
    }

    protected Response execute() throws IOException {
        Request request = this.builder.build();
        OkHttpClient client = new OkHttpClient();
        return client.newCall(request).execute();
    }

    protected enum RequestType {
        POST,
        GET,
        DELETE
    }
}
