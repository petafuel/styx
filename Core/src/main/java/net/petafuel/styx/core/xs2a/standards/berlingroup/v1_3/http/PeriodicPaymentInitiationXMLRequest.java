package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PeriodicPaymentMultipartBodySerializer;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class PeriodicPaymentInitiationXMLRequest extends XS2APaymentInitiationRequest {

    private RequestBody body;

    public PeriodicPaymentInitiationXMLRequest(PaymentInitiationPain001Request xmlRequest, PeriodicPayment periodicPayment) {
        super(xmlRequest.getPaymentProduct(), xmlRequest.getPaymentService(), xmlRequest.getPsu());

        Headers headersXml = new Headers.Builder()
                .add("Content-Disposition", "form-data; name=\"xml_sct\"")
                .build();
        MultipartBody.Part xmlPart = MultipartBody.Part.create(headersXml, RequestBody.create(xmlRequest.getRawBody().orElse(""), MediaType.get("application/xml; charset=utf-8")));

        Gson gson = new GsonBuilder().registerTypeAdapter(PeriodicPayment.class, new PeriodicPaymentMultipartBodySerializer()).create();

        Headers headersJson = new Headers.Builder()
                .add("Content-Disposition", "form-data; name=\"json_standingorderManagement\"")
                .build();
        MultipartBody.Part jsonPart = MultipartBody.Part.create(headersJson, RequestBody.create(gson.toJson(periodicPayment), MediaType.get("application/json; charset=utf-8")));

        //add a uuid as boundary to avoid boundary repetition in http message
        this.body = new MultipartBody.Builder(UUID.randomUUID().toString())
                .addPart(xmlPart)
                .addPart(jsonPart)
                .setType(MultipartBody.FORM)
                .build();
    }

    @Override
    public Optional<String> getRawBody() {
        final Buffer buffer = new Buffer();
        try {
            //build raw multipartBody with xml and json
            body.writeTo(buffer);
            return Optional.of(buffer.readUtf8());
        } catch (IOException e) {
            //TODO error handling
            return Optional.empty();
        }
    }

    public RequestBody getBody() {
        return body;
    }
}
